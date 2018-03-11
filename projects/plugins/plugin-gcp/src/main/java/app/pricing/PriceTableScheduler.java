package app.pricing;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import app.common.SystemConstants;
import app.common.utils.DateArithmeticUtil;
import app.common.utils.DateCompareUtil;
import app.models.PriceModel;
import app.models.PriceTableModel;
import app.models.PriceTableStatusModel;
import app.models.PriceTableStatusModel.Status;
import app.pricing.exceptions.PriceTableDateInvalidException;
import app.pricing.exceptions.PriceTableVersionException;

@Component
public class PriceTableScheduler {
    
    private static Lock _lock_ = new ReentrantLock();
    private static PriceModel pricingModel = null;
    private static PriceTableStatusModel pricingStatusModel = 
            PriceTableStatusModel.createProcessingStatus();
    
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Main scheduler call
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    @Scheduled(fixedDelay = 1 * 60 * 1000) // 1 minute
    public void updatePriceTable() {
        Calendar now = Calendar.getInstance();  
        PriceTableStatusModel newStatus = null;
        try {
            _lock_.lock();
            
            // First execution
            if(pricingModel == null && existsPriceTableFile()){
                newStatus = updatePricing(now);
                if(newStatus.getStatus() == Status.OK 
                        && isDateOlderThan1Day(pricingModel.getLastUpdate(), now.getTime())) {
                    newStatus.setLastSearch(null);
                } else {
                    pricingStatusModel = newStatus;                    
                }
            }            
            if(priceMustBeUpdated(now.getTime()) || !existsPriceTableFile()) {
                downloadPriceTableFile();  
                pricingStatusModel = updatePricing(now);              
            } 
        } catch (IOException e) {
            pricingStatusModel = 
                    PriceTableStatusModel.createDownloadErrorStatus(
                                now.getTime(), e.getMessage());     
        }
        finally {
            _lock_.unlock();
        } 
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Private methods synchronised with the main scheduler call
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    private boolean priceMustBeUpdated(final Date now) {
        if(pricingStatusModel.getStatus() != Status.OK
                || pricingStatusModel.getLastSearch() == null 
                || pricingModel == null) {
            return true;
        }                
        return isDateOlderThan1Day(
                pricingStatusModel.getLastSearch(), now);
    }
    
    private boolean isDateOlderThan1Day(final Date date, final Date now) {
        Date nextSearch = DateArithmeticUtil.from(date).add(1).days();        
        if(DateCompareUtil.is(now)
                .greaterThan(nextSearch)) {
            return true;
        }        
        return false;
    }
    
    private PriceTableStatusModel updatePricing(final Calendar now) {    
        try {
            PriceTableParser parser = new PriceTableParser(
                    SystemConstants.PRICE_TABLE_FILE,
                    SystemConstants.PRICE_TABLE_VERSION);
            pricingModel = parser.parse();
            return PriceTableStatusModel.createOkStatus(now.getTime());
        } catch (ParseException | PriceTableDateInvalidException e) {
            return PriceTableStatusModel.createDateErrorStatus(now.getTime());
        } catch (PriceTableVersionException e) {
            return PriceTableStatusModel.createVersionErrorStatus(now.getTime(), e.getMessage());
        } catch (IOException e) {
            return PriceTableStatusModel.createParseErrorStatus(now.getTime(), e.getMessage());
        }  
    }
    
    private void downloadPriceTableFile() 
            throws FileNotFoundException, IOException { 
        URL website;
        website = new URL(SystemConstants.PRICE_TABLE_URL);
        ReadableByteChannel rbc = Channels.newChannel(website.openStream());
        createDirForPriceTableFile();    
        try(FileOutputStream fos = new FileOutputStream(
                SystemConstants.PRICE_TABLE_FILE)){
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        }
    }

    private boolean existsPriceTableFile() {
        File f = new File(SystemConstants.PRICE_TABLE_FILE);
        if(f.exists() && !f.isDirectory()) { 
            return true;
        }
        return false;
    }
    
    private void createDirForPriceTableFile() {
        File directory = new File(SystemConstants.PRICE_TABLE_DIR);
        if (! directory.exists()) {
            directory.mkdir();
        }
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Non blocking methods 
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    public static PriceTableModel getPricing() {
        if(_lock_.tryLock()) {
            try {
                return new PriceTableModel(pricingStatusModel, pricingModel);                
            }
            finally {
                _lock_.unlock();
            }
        }
        else {
            return new PriceTableModel(getLastSearchForProcessingStatus(), null);
        }
    }       
    
    public static PriceTableStatusModel getPricingStatus() {
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
    
    private static PriceTableStatusModel getLastSearchForProcessingStatus() {
        PriceTableStatusModel pricingStatus = 
                PriceTableStatusModel.createProcessingStatus();
        if(pricingStatusModel != null) {
            pricingStatus.setLastSearch(pricingStatusModel.getLastSearch());
        }
        return pricingStatus;
    }
    
}
