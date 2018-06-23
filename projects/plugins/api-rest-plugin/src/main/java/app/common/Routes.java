package app.common;

public class Routes {

    /*
     * AbstractInfoController
     */
    public static final String INFO = "/info";

    /*
     * AbstractNetworkController
     */
    public static final String FIREWALLS = "/firewalls";
    public static final String FIREWALLS_NAME = Routes.FIREWALLS+"/{name}";
    public static final String FIREWALLS_NAME_ = Routes.FIREWALLS+"/{name:.+}";

    /*
     * AbstractImageController
     */
    public static final String IMAGES = "/images";
    public static final String IMAGES_NAME = Routes.IMAGES+"/{name}";
    public static final String IMAGES_NAME_ = Routes.IMAGES+"/{name:.+}";

    /*
     * AbstractPricingController
     */
    public static final String PRICING = "/pricing";
    public static final String PRICING_STATUS = "/pricing/status";

    /*
     * AbstractInstanceController
     */
    public static final String INSTANCES = "/instances";
    public static final String INSTANCES_ZONE_NAME = Routes.INSTANCES + "/{zone}" + "/{name}";
    public static final String INSTANCES_ZONE_NAME_ = Routes.INSTANCES + "/{zone:.+}" + "/{name:.+}";

    /*
     * AbstractInstanceZoneController
     */
    public static final String INSTANCE_ZONES = "/instance/zones";

    /*
     * AbstractInstanceRegionController
     */
    public static final String INSTANCE_REGIONS = "/instance/regions";
    public static final String INSTANCE_REGIONS_ZONES = "/instance/regions/{name}/zones";
    public static final String INSTANCE_REGIONS_ZONES_ = "/instance/regions/{name:.+}/zones";

    /*
     * AbstractStorageController
     */
    public static final String SPACES = "/spaces";
    public static final String SPACES_NAME = "/spaces/{name}";
    public static final String SPACES_NAME_ = "/spaces/{name:.+}";
    public static final String SPACES_NAME_FILE_UPLOAD = "/spaces/{name}/file/{file}/upload/url";
    public static final String SPACES_NAME_FILE_UPLOAD_ = "/spaces/{name:.+}/file/{file:.+}/upload/url";
    public static final String SPACES_NAME_FILE_DOWNLOAD = "/spaces/{name}/file/{file}/download/url";
    public static final String SPACES_NAME_FILE_DOWNLOAD_ = "/spaces/{name:.+}/file/{file:.+}/download/url";
}
