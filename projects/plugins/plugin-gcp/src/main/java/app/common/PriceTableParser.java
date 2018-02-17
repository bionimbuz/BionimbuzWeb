package app.common;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

import app.models.PricingModel;
import app.models.pricing.InstancePricing;
import app.models.pricing.ZonePricing;

public class PriceTableParser {

    private static final String DD_MMMMM_YYYY = "dd-MMMMM-yyyy";
    private static final String TAG_SSD = "ssd";
    private static final String TAG_MEMORY = "memory";
    private static final String TAG_CORES = "cores";
    private static final String TAG_GCP_PRICE_LIST = "gcp_price_list";
    private static final String TAG_UPDATED = "updated";
    private static final String SUBSTR_VMIMAGE = "-VMIMAGE-";
    
    private String filePath;

    public PriceTableParser(final String filePath) {
        this.filePath = filePath;
    }

    public String getFilePath() {
        return filePath;
    }
    
    public PricingModel parse() throws JsonParseException, IOException, ParseException {
    
        try(JsonParser jsonParser = 
                new JsonFactory().createParser(new File(filePath))) {
        
            PricingModel res = new PricingModel();
            while(jsonParser.nextToken() != JsonToken.END_OBJECT) {  
                String name = jsonParser.getCurrentName();
                if(TAG_UPDATED.equals(name)){
                    jsonParser.nextToken();
                    DateFormat df = new SimpleDateFormat(
                            DD_MMMMM_YYYY, Locale.ENGLISH);
                    String value = jsonParser.getText();
                    res.setLastUpdate(df.parse(value));
                } else if(TAG_GCP_PRICE_LIST.equals(name)){
                    jsonParser.nextToken();
                    res.setListInstancePricing(
                            parseInstancePricing(jsonParser));
                }
            }
            return res;
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
