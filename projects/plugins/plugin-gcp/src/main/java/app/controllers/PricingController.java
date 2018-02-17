package app.controllers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.text.ParseException;
import java.util.Calendar;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import app.common.PriceTableParser;
import app.common.SystemConstants;
import app.models.Body;
import app.models.PricingModel;

@RestController
public class PricingController extends AbstractPricingController{  
    
    private PricingModel pricingModel = null;
    
    /*
     * Overwritten Methods
     */
    
    @Override
    protected ResponseEntity<Body<PricingModel>> getPricing(
            final String token, 
            final String identity) throws Exception {   
        
        if(priceMustBeUpdated()) {
            updatePricing();
        }
        
        return ResponseEntity.ok(
                Body.create(pricingModel));
    }
    
    private boolean priceMustBeUpdated() {
        if(pricingModel == null)
            return true;
        
        Calendar now = Calendar.getInstance();
        Calendar lastSearch = Calendar.getInstance();
        lastSearch.setTime(pricingModel.getLastSearch());
        lastSearch.add(Calendar.DATE, 1);
        
        if(now.compareTo(lastSearch) > 0) {
            return true;
        }
        
        return false;
    }
    
    private void updatePricing() {      
        Calendar now = Calendar.getInstance();
        if(pricingModel == null)
            pricingModel = new PricingModel(now.getTime(), null);  
        
        if(!downloadPriceTableFile())
            return;
        
        PriceTableParser parser = new PriceTableParser(
                SystemConstants.PRICE_TABLE_FILE);
        
        try {
            pricingModel = parser.parse();
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        pricingModel.setLastSearch(now.getTime());
    }
    
    private boolean downloadPriceTableFile() {
        try {        
            URL website;
            website = new URL(SystemConstants.PRICE_TABLE_URL);
            ReadableByteChannel rbc = Channels.newChannel(website.openStream());
            createDirForPriceTable();    
            try(FileOutputStream fos = new FileOutputStream(
                    SystemConstants.PRICE_TABLE_FILE)){
                fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
            }
            return true;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void createDirForPriceTable() {
        File directory = new File(SystemConstants.PRICE_TABLE_DIR);
        if (! directory.exists())
            directory.mkdir();
    }
    

}
