package app.common;

public class Routes {

    /*
     * AbstractInfoController
     */
    public static final String INFO = "/info";

    /*
     * AbstractImageController
     */
    public static final String IMAGES = "/images";
    public static final String IMAGES_NAME = IMAGES+"/{name}";
    public static final String IMAGES_NAME_ = IMAGES+"/{name:.+}";

    /*
     * AbstractPricingController
     */
    public static final String PRICING = "/pricing";
    public static final String PRICING_STATUS = PRICING+"/status";

    /*
     * AbstractComputingController
     */
    public static final String COMPUTING = "/computing";
    public static final String REGIONS = "/regions";
    public static final String ZONES = "/zones";
    public static final String INSTANCES = "/instances";
    
    public static final String COMPUTING_INSTANCES = COMPUTING + INSTANCES;
    public static final String COMPUTING_REGIONS_ZONES_INSTANCES_NAME = 
            COMPUTING + REGIONS + "/{region}" + ZONES + "/{zone}" + INSTANCES + "/{name}";
    public static final String COMPUTING_REGIONS_ZONES_INSTANCES_NAME_ = 
            COMPUTING + REGIONS + "/{region:.+}" + ZONES + "/{zone:.+}" + INSTANCES + "/{name:.+}";
    public static final String COMPUTING_REGIONS = COMPUTING + REGIONS;
    public static final String COMPUTING_REGIONS_ZONES = COMPUTING + REGIONS + "/{name}" + ZONES;
    public static final String COMPUTING_REGIONS_ZONES_ = COMPUTING + REGIONS + "/{name:.+}" + ZONES;

    /*
     * AbstractStorageController
     */
    public static final String STORAGE = "/storage";
    public static final String SPACES = "/spaces";
    public static final String FILES = "/files";
    public static final String DOWNLOAD_URL = "/download/url";
    public static final String UPLOAD_URL = "/upload/url";
    
    public static final String STORAGE_SPACES = STORAGE + SPACES;
    public static final String SPACES_NAME = STORAGE_SPACES + "/{name}";
    public static final String SPACES_NAME_ = STORAGE_SPACES + "/{name:.+}";
    public static final String SPACES_NAME_FILE_UPLOAD = 
            STORAGE_SPACES + "/{name}" + FILES + "/{file}" + UPLOAD_URL;
    public static final String SPACES_NAME_FILE_UPLOAD_ = 
            STORAGE_SPACES + "/{name:.+}" + FILES + "/{file:.+}" + UPLOAD_URL;
    public static final String SPACES_NAME_FILE_DOWNLOAD = 
            STORAGE_SPACES + "/{name}" + FILES + "/{file}" + DOWNLOAD_URL;
    public static final String SPACES_NAME_FILE_DOWNLOAD_ = 
            STORAGE_SPACES + "/{name:.+}" + FILES + "/{file:.+}" + DOWNLOAD_URL;

    /*
     * StatusController
     */
    public static final String IMALIVE = "/imalive";
}
