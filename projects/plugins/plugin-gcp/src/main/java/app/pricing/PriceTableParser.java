package app.pricing;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.jclouds.googlecloudstorage.domain.DomainResourceReferences.Location;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

import app.models.PluginPriceModel;
import app.models.pricing.InstanceTypePricing;
import app.models.pricing.StoragePricing;
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
    private static final String TAG_STORAGE_REGIONAL = "CP-BIGSTORE-STORAGE-REGIONAL";
    private static final String TAG_STORAGE_A_CLASS = "CP-BIGSTORE-CLASS-A-REQUEST";
    private static final String TAG_STORAGE_B_CLASS = "CP-BIGSTORE-CLASS-B-REQUEST";
    private Set<String> storageLocations;

    private String filePath;
    private String expectedVersion;

    private enum StoragePriceType {
        PRICE_REGIONAL,
        PRICE_CLASS_A,
        PRICE_CLASS_B
    }

    public PriceTableParser(
            final String filePath,
            final String expectedVersion) {
        this.filePath = filePath;
        this.expectedVersion = expectedVersion;
        fillStorageLocations();
    }

    private void fillStorageLocations() {
        String strLocation;
        storageLocations = new TreeSet<>();
        for(Location location : Location.values()) {
            strLocation = location.toString().toLowerCase();
            if(!strLocation.contains("-"))
                continue;
            storageLocations.add(strLocation);
        }
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
                if(TAG_VERSION.equals(name)){
                    jsonParser.nextToken();
                    String value = jsonParser.getText();
                    if(!value.equals(expectedVersion)) {
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
                    parsePrices(
                            jsonParser, instancePricing, storagePricing);
                }
            }
            if(lastUpdate == null) {
                throw new PriceTableDateInvalidException();
            }
            filterStorageLocationsWithoutPrices(storagePricing);
            return new PluginPriceModel(
                    lastUpdate,
                    instancePricing,
                    storagePricing);
        }
    }

    private void parsePrices(
            JsonParser jsonParser,
            HashMap<String, InstanceTypePricing> listInstancePricing,
            HashMap<String, StoragePricing> listStoragePricing)
            throws IOException {
        while(jsonParser.nextToken() != JsonToken.END_OBJECT) {
            String tagName = jsonParser.getCurrentName();
            // Reads only VM contents
            if(tagName.contains(SUBSTR_VMIMAGE)) {
                parseInstance(jsonParser, listInstancePricing, tagName);
            } else if(TAG_STORAGE_REGIONAL.equals(tagName)) {
                parseStorageRegional(jsonParser, listStoragePricing,
                        StoragePriceType.PRICE_REGIONAL);
            } else if(TAG_STORAGE_A_CLASS.equals(tagName)) {
                parseStorageRegional(jsonParser, listStoragePricing,
                        StoragePriceType.PRICE_CLASS_A);
            } else if(TAG_STORAGE_B_CLASS.equals(tagName)) {
                parseStorageRegional(jsonParser, listStoragePricing,
                        StoragePriceType.PRICE_CLASS_B);
            } else {
                jsonParser.nextToken();
                jsonParser.skipChildren();
            }
        }
    }

    private void parseStorageRegional(
            JsonParser jsonParser,
            HashMap<String, StoragePricing> listStoragePricing,
            final StoragePriceType storagePriceType) throws IOException {

        jsonParser.nextToken();
        Double pricing = null;
        while(jsonParser.nextToken() != JsonToken.END_OBJECT) {
            String zoneName = jsonParser.getCurrentName();
            if(!storageLocations.contains(zoneName)) {
                jsonParser.nextToken();
                continue;
            }
            StoragePricing storagePrice =
                    listStoragePricing.get(zoneName);
            if(null == storagePrice) {
                storagePrice = new StoragePricing(zoneName);
                listStoragePricing.put(zoneName, storagePrice);
            }

            jsonParser.nextToken();
            pricing = Double.valueOf(jsonParser.getText());
            switch(storagePriceType) {
                case PRICE_REGIONAL:
                    storagePrice.setPrice(pricing);
                    break;
                case PRICE_CLASS_A:
                    storagePrice.setClassAPrice(pricing);
                    break;
                case PRICE_CLASS_B:
                    storagePrice.setClassBPrice(pricing);
                    break;
            }
        }
    }

    private void parseInstance(JsonParser jsonParser,
            HashMap<String, InstanceTypePricing> listInstancePricing,
            String tagName) throws IOException {

        InstanceTypePricing instancePricing;
        HashMap<String, Double> zones = new HashMap<>();
        tagName = processVmTypeName(tagName);
        jsonParser.nextToken();
        boolean zonesRead = false;
        Short cores = 0;
        Double memory = 0D;
        Double pricing = 0D;
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
                zones.put(vmContentName, pricing);
            } else if(TAG_MEMORY.equals(vmContentName)) {
                jsonParser.nextToken();
                memory = Double.valueOf(jsonParser.getText());
            } else if(TAG_SSD.equals(vmContentName)) {
                jsonParser.nextToken();
                // Skip array values
                while (jsonParser.nextToken() != JsonToken.END_ARRAY) {}
                instancePricing = new InstanceTypePricing(
                        tagName, cores, memory, zones);
                listInstancePricing.put(tagName, instancePricing);
            }
        }
    }

    private void filterStorageLocationsWithoutPrices(
            HashMap<String, StoragePricing> listStoragePricing) {
        for (Iterator<Map.Entry<String, StoragePricing>> it =
                    listStoragePricing.entrySet().iterator();
                it.hasNext();) {
            Map.Entry<String, StoragePricing> entry = it.next();
            if (null == entry.getValue().getPrice()) {
                it.remove();
            }
        }
    }

    private String processVmTypeName(final String instanceName) {
        return instanceName.substring(
                instanceName.indexOf(SUBSTR_VMIMAGE) + SUBSTR_VMIMAGE.length())
                    .toLowerCase();
    }
}
