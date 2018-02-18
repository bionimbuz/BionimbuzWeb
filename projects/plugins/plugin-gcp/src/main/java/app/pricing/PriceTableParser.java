package app.pricing;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

import app.models.PricingModel;
import app.models.PricingStatusModel;
import app.models.PricingStatusModel.Status;
import app.models.pricing.InstancePricing;
import app.models.pricing.ZonePricing;
import app.pricing.exceptions.PriceTableDateInvalidException;
import app.pricing.exceptions.PriceTableVersionException;

public class PriceTableParser {

    private static final String DD_MMMMM_YYYY = "dd-MMMMM-yyyy";
    private static final String TAG_SSD = "ssd";
    private static final String TAG_MEMORY = "memory";
    private static final String TAG_CORES = "cores";
    private static final String TAG_GCP_PRICE_LIST = "gcp_price_list";
    private static final String TAG_UPDATED = "updated";
    private static final String TAG_VERSION = "version";
    private static final String SUBSTR_VMIMAGE = "-VMIMAGE-";
    
    private String filePath;
    private String expectedVersion;

    public PriceTableParser(
            final String filePath, 
            final String expectedVersion) {
        this.filePath = filePath;
        this.expectedVersion = expectedVersion;
    }

    public String getFilePath() {
        return filePath;
    }
    
    private boolean needUpdate(
            final PricingModel lastPricing, 
            final Date lastUpdateFromParse) {
        if(lastPricing == null)
            return true;
        if(lastPricing.getStatus().getStatus() != Status.OK)
            return true;
        
        Calendar lastUpdate = Calendar.getInstance();
        lastUpdate.setTime(lastPricing.getLastUpdate());
        Calendar lastUpdateFound = Calendar.getInstance();
        lastUpdateFound.setTime(lastUpdateFromParse);        
        if(lastUpdate.compareTo(lastUpdateFound) != 0)
            return true;
        return false;
    }
    
    public PricingModel parse(
            final Date now, 
            final PricingModel lastPricing) {

        HashMap<String, InstancePricing> instancePricing = new HashMap<>(); 
        Date lastUpdate = null;
        
        try(JsonParser jsonParser = 
                new JsonFactory().createParser(new File(filePath))) {
        
            while(jsonParser.nextToken() != JsonToken.END_OBJECT) {  
                String name = jsonParser.getCurrentName();
                if(TAG_VERSION.equals(name)){
                    jsonParser.nextToken();
                    String value = jsonParser.getText();                    
                    if(value.equals(expectedVersion)) {
                        throw new PriceTableVersionException(value, expectedVersion);
                    }
                } else if(TAG_UPDATED.equals(name)){
                    jsonParser.nextToken();
                    DateFormat df = new SimpleDateFormat(
                            DD_MMMMM_YYYY, Locale.ENGLISH);
                    lastUpdate = df.parse(jsonParser.getText());
                } else if(TAG_GCP_PRICE_LIST.equals(name)){
                    jsonParser.nextToken();    
                    if(lastUpdate == null) {
                        throw new PriceTableDateInvalidException();
                    }
                    if(needUpdate(lastPricing, lastUpdate)) {
                        instancePricing = parseInstancePricing(jsonParser);
                    }
                    else {
                        instancePricing = lastPricing.getListInstancePricing();
                        lastUpdate  = lastPricing.getLastUpdate();
                    }
                }
            }
            
            if(lastUpdate == null) {
                throw new PriceTableDateInvalidException();
            }
            
            return new PricingModel(
                    PricingStatusModel.createOkStatus(now),
                    lastUpdate, 
                    instancePricing);            
        } catch (ParseException | PriceTableDateInvalidException e) {
            return new PricingModel(
                    PricingStatusModel.createDateErrorStatus(now));
        } catch (PriceTableVersionException e) {
            return new PricingModel(
                    PricingStatusModel.createVersionErrorStatus(now, e.getMessage()));
        } catch (IOException e) {
            return new PricingModel(
                    PricingStatusModel.createParseErrorStatus(now, e.getMessage()));
        }      
    }
    
    private HashMap<String, InstancePricing> parseInstancePricing(JsonParser jsonParser) 
            throws IOException {
        boolean zonesRead = false;
        Short cores = 0;
        Double memory = 0D;
        Double pricing = 0D;
        ZonePricing zone = null;
        InstancePricing instancePricing = null;
        HashMap<String, InstancePricing> res = new HashMap<>();  
        HashMap<String, ZonePricing> zones = new HashMap<>();
        while(jsonParser.nextToken() != JsonToken.END_OBJECT) {            
            String instanceName = jsonParser.getCurrentName();
            // Reads only VM contents
            if(instanceName.contains(SUBSTR_VMIMAGE)) {
                instanceName = processVmTypeName(instanceName);                
                jsonParser.nextToken();
                zonesRead = false;
                // Reads the internal VM image section
                while(jsonParser.nextToken() != JsonToken.END_OBJECT) {
                    String vmContentName = jsonParser.getCurrentName();
                    if(!zonesRead && TAG_CORES.equals(vmContentName)) {
                        jsonParser.nextToken();
                        cores = (short)jsonParser.getValueAsInt(0);
                        zonesRead = true;
                    } else if (!zonesRead) {
                        jsonParser.nextToken();
                        pricing = Double.valueOf(jsonParser.getText());
                        zone = new ZonePricing(vmContentName, pricing);
                        zones.put(vmContentName, zone);
                    } else if(TAG_MEMORY.equals(vmContentName)) {
                        jsonParser.nextToken();
                        memory = Double.valueOf(jsonParser.getText());
                    } else if(TAG_SSD.equals(vmContentName)) {
                        jsonParser.nextToken();
                        // Skip array values
                        while (jsonParser.nextToken() != JsonToken.END_ARRAY) {}
                        instancePricing = new InstancePricing(
                                instanceName, cores, memory, zones);
                        res.put(instanceName, instancePricing);
                    }
                }                
            } else {
                jsonParser.nextToken();
                jsonParser.skipChildren();
            }
        }
        return res;
    }

    private String processVmTypeName(final String instanceName) {
        return instanceName.substring(
                instanceName.indexOf(SUBSTR_VMIMAGE) + SUBSTR_VMIMAGE.length())
                    .toLowerCase();
    }
}
