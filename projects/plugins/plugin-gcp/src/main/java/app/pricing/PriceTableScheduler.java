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
import app.models.pricing.StatusPricing;

@Component
public class PriceTableScheduler {
    
    private static Lock _lock_ = new ReentrantLock();
    private static PricingModel pricingModel = null;
    
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // 24 hours main scheduler call
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    @Scheduled(fixedDelay = 24 * 60 * 60 * 1000) // 24 hours
    public void updatePriceTable() {
        try {
            _lock_.lock();
            Calendar now = Calendar.getInstance();
            updatePricing(now);
        }
        finally {
            _lock_.unlock();
        } 
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Private methods synchronised with the main scheduler call
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    private void updatePricing(final Calendar now) {    
        try {
            downloadPriceTableFile();
            PriceTableParser parser = new PriceTableParser(
                    SystemConstants.PRICE_TABLE_FILE,
                    SystemConstants.PRICE_TABLE_VERSION);
        
            pricingModel = parser.parse(now.getTime(), pricingModel);
        } catch (IOException e) {
            pricingModel = new PricingModel(
                    StatusPricing.createDownloadErrorStatus(
                            now.getTime(), e.getMessage()));
        }
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
    // Public non blocking public methods 
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
            return new PricingModel(StatusPricing.createProcessingStatus());
        }
    }   
}
