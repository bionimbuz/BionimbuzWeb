package app.pricing;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

import app.common.Pair;
import app.models.PluginPriceModel;
import app.models.pricing.InstanceTypePricing;
import app.models.pricing.StoragePricing;
import app.pricing.exceptions.PriceTableDateInvalidException;
import app.pricing.exceptions.PriceTableVersionException;

public class PriceTableParser {

    private static final String TAG_PRICE_DIMENSIONS = "priceDimensions";
    private static final String TAG_PRICE_PER_UNIT = "pricePerUnit";
    private static final String VAL_HRS = "Hrs";
    private static final String TAG_UNIT = "unit";
    private static final String VAL_GENERAL_PURPOSE = "General purpose";
    private static final String TAG_INSTANCE_FAMILY = "instanceFamily";
    private static final String TAG_ON_DEMAND = "OnDemand";
    private static final String TAG_INSTANCE_TYPE = "instanceType";
    private static final String VAL_AMAZON_EC2 = "AmazonEC2";
    private static final String VAL_64_BIT = "64-bit";
    private static final String VAL_LINUX = "Linux";
    private static final String VAL_NA = "NA";
    private static final String VAL_SHARED = "Shared";
    private static final String TAG_MEMORY = "memory";
    private static final String TAG_VCPU = "vcpu";
    private static final String TAG_LOCATION = "location";
    private static final String TAG_TENANCY = "tenancy";
    private static final String TAG_PRE_INSTALLED_SW = "preInstalledSw";
    private static final String TAG_OPERATING_SYSTEM = "operatingSystem";
    private static final String TAG_PROCESSOR_ARCHITECTURE = "processorArchitecture";
    private static final String TAG_SERVICECODE = "servicecode";
    private static final String TAG_ATTRIBUTES = "attributes";
    private static final String VAL_COMPUTE_INSTANCE = "Compute Instance";
    private static final String TAG_PRODUCT_FAMILY = "productFamily";
    private static final String TAG_FORMAT_VERSION = "formatVersion";
    private static final String TAG_PUBLICATION_DATE = "publicationDate";
    private static final String TAG_PRODUCTS = "products";
    private static final String TAG_TERMS = "terms";
    private static final String YYYY_MM_DD = "yyyy-MM-dd";
    private static Map<String, String> REGIONS = new HashMap<>();
    private HashMap<String, Pair<String, String>> listSkuRegionPrice = new HashMap<>();
    static {        
        REGIONS.put("Asia Pacific (Mumbai)", "ap-south-1");
        REGIONS.put("Asia Pacific (Seoul)", "ap-northeast-2");
        REGIONS.put("Asia Pacific (Singapore)", "ap-southeast-1");
        REGIONS.put("Asia Pacific (Sydney)", "ap-southeast-2");
        REGIONS.put("Asia Pacific (Tokyo)", "ap-northeast-1");
        REGIONS.put("Canada (Central)", "ca-central-1");
        REGIONS.put("EU (Frankfurt)", "eu-central-1");
        REGIONS.put("EU (Ireland)", "eu-west-1");
        REGIONS.put("EU (London)", "eu-west-2");
        REGIONS.put("EU (Paris)", "eu-west-3");
        REGIONS.put("South America (Sao Paulo)", "sa-east-1");
        REGIONS.put("US East (N. Virginia)", "us-east-1");
        REGIONS.put("US East (Ohio)", "us-east-2");
        REGIONS.put("US West (N. California)", "us-west-1");
        REGIONS.put("US West (Oregon)", "us-west-2");        
    }

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

    public PluginPriceModel parse()
                    throws JsonParseException, IOException,
                    PriceTableVersionException, ParseException,
                    PriceTableDateInvalidException {

        HashMap<String, InstanceTypePricing> instancePricing = new HashMap<>();
        HashMap<String, StoragePricing> storagePricing = new HashMap<>();
        Date lastUpdate = null;
        try(JsonParser jsonParser =
                new JsonFactory().createParser(new File(filePath))) {

            while(jsonParser.nextToken() != JsonToken.END_OBJECT) {
                String name = jsonParser.getCurrentName();
                if(name == null) {
                    continue;
                }
                if(TAG_FORMAT_VERSION.equals(name)){
                    jsonParser.nextToken();
                    String value = jsonParser.getText();
                    if(!value.equals(expectedVersion)) {
                        throw new PriceTableVersionException(value, expectedVersion);
                    }
                } else if(TAG_PUBLICATION_DATE.equals(name)){
                    jsonParser.nextToken();
                    DateFormat df = new SimpleDateFormat(
                            YYYY_MM_DD, Locale.ENGLISH);
                    lastUpdate = df.parse(jsonParser.getText());
                } else if(TAG_PRODUCTS.equals(name)){   
                    parseInstanceTypes(jsonParser, instancePricing);     
                } else if(TAG_TERMS.equals(name)){
                    parseInstancePrices(jsonParser, instancePricing);                     
                } else {
                    jsonParser.nextToken();
                }
            }
            if(lastUpdate == null) {
                throw new PriceTableDateInvalidException();
            }
            return new PluginPriceModel(
                    lastUpdate,
                    instancePricing,
                    storagePricing);
        }
    } 
    
    private void parseInstanceTypes(
            JsonParser jsonParser, 
            HashMap<String, InstanceTypePricing> instancePricing) throws IOException {
        
        String name = "", value = "", sku = "", region = "", type = "";
        Short cores = 0;
        Double memory = 0d;
        boolean addNode = true;
        
        jsonParser.nextToken();
        while(jsonParser.nextToken() != JsonToken.END_OBJECT) {
            
            sku = jsonParser.getCurrentName();
            jsonParser.nextToken();
            addNode = true;
            while(jsonParser.nextToken() != JsonToken.END_OBJECT) {

                jsonParser.nextToken();
                
                name = jsonParser.getCurrentName();
                value = jsonParser.getText();
                if(name.equals(TAG_PRODUCT_FAMILY) && !value.equals(VAL_COMPUTE_INSTANCE)) {                    
                    forwardNode(jsonParser);
                    break;
                }
                if(!name.equals(TAG_ATTRIBUTES))
                {
                    jsonParser.skipChildren();
                    continue;
                }

                region = "";
                cores = 0;
                memory = 0d;
                while(jsonParser.nextToken() != JsonToken.END_OBJECT) {    
                    jsonParser.nextToken();                    
                    name = jsonParser.getCurrentName();
                    value = jsonParser.getText();                    
                    if(!nodeMustBeProcessed(name, value)) {                    
                        forwardNode(jsonParser);
                        addNode = false;
                        break;
                    } else if(name.equals(TAG_INSTANCE_TYPE)) {
                        type = value;
                    } else if(name.equals(TAG_LOCATION)) {
                        region = value;
                    } else if(name.equals(TAG_VCPU)) {
                        cores = Short.valueOf(value);
                    } else if(name.equals(TAG_MEMORY)) {
                        memory = Double.valueOf(value.replace(',',  '.').split(" ")[0]);
                    }              
                }
                jsonParser.skipChildren();                
                if(!addNode)
                    continue;       
                region = REGIONS.get(region);
                if(region == null)
                    continue;         
                
                InstanceTypePricing pricing = 
                        instancePricing.get(type);
                if(pricing == null) {
                    pricing = new InstanceTypePricing(
                            type, 
                            cores, 
                            memory);                
                    instancePricing.put(type, pricing);        
                }  
                listSkuRegionPrice.put(sku, new Pair<String, String>(type, region));
            }
        }
    }

    private void parseInstancePrices(
            JsonParser jsonParser,
            HashMap<String, InstanceTypePricing> instancePricing) throws IOException {
        String name = "", value = "", sku = "";
        Double price = -1d;

        jsonParser.nextToken();
        // Level: OnDemand
        while(jsonParser.nextToken() != JsonToken.END_OBJECT) {  
            name = jsonParser.getCurrentName();
            if(!name.equals(TAG_ON_DEMAND)) {                    
                forwardNode(jsonParser);
                break;
            }
            
            price = -1d;
            
            jsonParser.nextToken();
            // Level: sku
            while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
                jsonParser.nextToken();
                sku = jsonParser.getCurrentName();                  
                if(!listSkuRegionPrice.containsKey(sku)) {
                    forwardNode(jsonParser);
                    continue;
                } 
                
                jsonParser.nextToken();
                // Level: sku + offer term
                while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
                    jsonParser.nextToken();
                    name = jsonParser.getCurrentName();
                    if(!name.equals(TAG_PRICE_DIMENSIONS)) {    
                        continue;
                    }

                    jsonParser.nextToken();
                    // Level: priceDimensions
                    while (jsonParser.nextToken() != JsonToken.END_OBJECT) {                        
                        
                        jsonParser.nextToken();
                        // Level: sku + offer term + id
                        while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
                            jsonParser.nextToken();
                            name = jsonParser.getCurrentName();
                            value = jsonParser.getText();

                            if(name.equals(TAG_UNIT) && !value.equals(VAL_HRS)) {
                                forwardNode(jsonParser);
                                break;
                            } else if (name.equals(TAG_PRICE_PER_UNIT)) {
                                jsonParser.nextToken();
                                jsonParser.nextToken();
                                value = jsonParser.getText();   
                                price = Double.valueOf(value);

                                forwardNode(jsonParser);
                                break;
                            }
                        }
                        forwardNode(jsonParser);
                    }
                    forwardNode(jsonParser);
                }
                
                if(price < 0)
                    continue;

                Pair<String, String> instTypeRegion = 
                        listSkuRegionPrice.get(sku);                
                
                instancePricing.get(instTypeRegion.getLeft())
                        .getListRegionPricing()
                                .put(instTypeRegion.getRight(), price);
            }            
        }
    }
    
    private static Boolean nodeMustBeProcessed(
            final String name, final String value) {
        if(name.equals(TAG_SERVICECODE) && !value.equals(VAL_AMAZON_EC2)) {                    
            return false;
        } else if(name.equals(TAG_INSTANCE_FAMILY) && !value.equals(VAL_GENERAL_PURPOSE)) {                     
            return false;
        } else if(name.equals(TAG_TENANCY) && !value.contains(VAL_SHARED)) {                       
            return false;
        } else if(name.equals(TAG_PROCESSOR_ARCHITECTURE) && !value.contains(VAL_64_BIT)) {                     
            return false;
        } else if(name.equals(TAG_OPERATING_SYSTEM) && !value.contains(VAL_LINUX)) {                      
            return false;
        } else if(name.equals(TAG_PRE_INSTALLED_SW) && !value.contains(VAL_NA)) {                        
            return false;
        } 
        return true;
    }

    private void forwardNode(JsonParser jsonParser) throws IOException {
        while(jsonParser.nextToken() != JsonToken.END_OBJECT) {
            jsonParser.nextToken();
            jsonParser.skipChildren();   
        }
    }

    public static void printPriceTable(
            HashMap<String, InstanceTypePricing> instancePricing) {
        for (Map.Entry<String, InstanceTypePricing> entry : instancePricing.entrySet())
        {
            System.out.println("---------------------------------------");
            System.out.println("Instance Type: " + entry.getKey());
            System.out.println("---------------------------------------");
            
            for (Map.Entry<String, Double> entry2 : 
                            entry.getValue().getListRegionPricing().entrySet())
            {
                if(entry2.getValue() < 0)                                
                    System.out.print("#### ");
                System.out.println(entry2.getKey() + ": " + entry2.getValue());
            }                        
        }
    }   
}
