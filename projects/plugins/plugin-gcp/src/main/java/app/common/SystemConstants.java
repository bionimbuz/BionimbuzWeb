package app.common;

public class SystemConstants {

    public static final String PLUGIN_VERSION = "0.1";
    public static final String PLUGIN_NAME = "Google Cloud Platform";

    public static final String PLUGIN_COMPUTE_READ_SCOPE = "https://www.googleapis.com/auth/compute.readonly";
    public static final String PLUGIN_COMPUTE_WRITE_SCOPE = "https://www.googleapis.com/auth/compute";

    public static final String PLUGIN_STORAGE_READ_SCOPE = "https://www.googleapis.com/auth/devstorage.read_only";
    public static final String PLUGIN_STORAGE_WRITE_SCOPE = "https://www.googleapis.com/auth/devstorage.read_write";

    public static final String CLOUD_COMPUTE_TYPE = "google-compute-engine";
    public static final String CLOUD_STORAGE_TYPE = "google-cloud-storage";

    public static final String IMAGE_PROJECT_UBUNTU = "ubuntu-os-cloud";

    public static final String PRICE_TABLE_URL = "https://cloudpricingcalculator.appspot.com/static/data/pricelist.json";
    public static final String PRICE_TABLE_DIR = "pricing/";
    public static final String PRICE_TABLE_FILE = PRICE_TABLE_DIR + "table.json";
    public static final String PRICE_TABLE_VERSION = "v1.44";

    public static final String STORAGE_FILE_UPLOAD_URL = "https://www.googleapis.com/upload/storage/v1/b/%s/o?uploadType=media&name=%s";
    public static final String STORAGE_FILE_DOWNLOAD_URL = "https://www.googleapis.com/storage/v1/b/%s/o/%s?alt=media";
}
