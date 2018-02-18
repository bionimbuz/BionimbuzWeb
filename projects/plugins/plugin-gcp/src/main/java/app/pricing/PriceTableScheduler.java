package app.pricing;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Calendar;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import app.common.SystemConstants;
import app.models.PricingModel;
import app.models.PricingStatusModel;
import app.models.PricingStatusModel.Status;

@Component
public class PriceTableScheduler {
    
    private static Lock _lock_ = new ReentrantLock();
    private static PricingModel pricingModel = null;
    private static PricingStatusModel pricingStatusModel = 
            PricingStatusModel.createProcessingStatus();
    
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // 30 min main scheduler call
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    @Scheduled(fixedDelay = 30 * 60 * 1000) // 24 hours
    public void updatePriceTable() {
        try {
            _lock_.lock();
            Calendar now = Calendar.getInstance();
            if(priceMustBeUpdated(now)) {
                updatePricing(now);
            }
        }
        finally {
            _lock_.unlock();
        } 
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Private methods synchronised with the main scheduler call
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    private boolean priceMustBeUpdated(final Calendar now) {
        if(pricingStatusModel.getStatus() != Status.OK
                || pricingStatusModel.getLastSearch() == null 
                || pricingModel == null) {
            return true;
        }
        
        Calendar lastSearch = Calendar.getInstance();
        lastSearch.setTime(pricingStatusModel.getLastSearch());
        lastSearch.add(Calendar.DATE, 1);
        
        if(now.compareTo(lastSearch) > 0) {
            return true;
        }        
        return false;
    }
    
    private void updatePricing(final Calendar now) {    
        PricingModel newPricingModel = null;
        try {
            downloadPriceTableFile();
            PriceTableParser parser = new PriceTableParser(
                    SystemConstants.PRICE_TABLE_FILE,
                    SystemConstants.PRICE_TABLE_VERSION);
        
            newPricingModel = parser.parse(now.getTime(), pricingModel);
        } catch (IOException e) {
            newPricingModel = new PricingModel(
                    PricingStatusModel.createDownloadErrorStatus(
                            now.getTime(), e.getMessage()));
        }
        
        pricingStatusModel = newPricingModel.getStatus();        
        // Get the latest prices available
        if(pricingModel!=null && pricingStatusModel.getStatus() != Status.OK) {
            newPricingModel.setLastUpdate(
                    pricingModel.getLastUpdate());
            newPricingModel.setListInstancePricing(
                    pricingModel.getListInstancePricing());
        }
        pricingModel = newPricingModel;
    }
    
    private void downloadPriceTableFile() 
            throws FileNotFoundException, IOException { 
        URL website;
        website = new URL(SystemConstants.PRICE_TABLE_URL);
        ReadableByteChannel rbc = Channels.newChannel(website.openStream());
        createDirForPriceTable();    
        try(FileOutputStream fos = new FileOutputStream(
                SystemConstants.PRICE_TABLE_FILE)){
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        }
    }

    private void createDirForPriceTable() {
        File directory = new File(SystemConstants.PRICE_TABLE_DIR);
        if (! directory.exists())
            directory.mkdir();
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Non blocking methods 
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    public static PricingModel getPricing() {
        if(_lock_.tryLock()) {
            try {
                return pricingModel;                
            }
            finally {
                _lock_.unlock();
            }
        }
        else {                        
            return new PricingModel(getLastSearchForProcessingStatus());
        }
    }       
    
    public static PricingStatusModel getPricingStatus() {
        if(_lock_.tryLock()) {
            try {
                return pricingStatusModel;                
            }
            finally {
                _lock_.unlock();
            }
        }
        else {    
            return getLastSearchForProcessingStatus();
        }
    }   
    
    private static PricingStatusModel getLastSearchForProcessingStatus() {
        PricingStatusModel pricingStatus = 
                PricingStatusModel.createProcessingStatus();
        if(pricingStatusModel != null) {
            pricingStatus.setLastSearch(pricingStatusModel.getLastSearch());
        }
        return pricingStatus;
    }
    
}
