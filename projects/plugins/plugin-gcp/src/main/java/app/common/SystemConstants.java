package app.common;

public class SystemConstants {    

    public static final String PLUGIN_VERSION = "0.1";
    public static final String PLUGIN_NAME = "Google Cloud Platform";
    public static final String PLUGIN_READ_SCOPE = "https://www.googleapis.com/auth/compute.readonly"; 
    public static final String PLUGIN_WRITE_SCOPE = "https://www.googleapis.com/auth/compute"; 
    
    public static final String CLOUD_TYPE = "google-compute-engine";
    public static final String IMAGE_PROJECT_UBUNTU = "ubuntu-os-cloud";
    
    public static final String PRICE_TABLE_URL = "https://cloudpricingcalculator.appspot.com/static/data/pricelist.json";
    public static final String PRICE_TABLE_DIR = "pricing/";
    public static final String PRICE_TABLE_FILE = PRICE_TABLE_DIR + "table.json";
    public static final String PRICE_TABLE_VERSION = "v1.23";
}
