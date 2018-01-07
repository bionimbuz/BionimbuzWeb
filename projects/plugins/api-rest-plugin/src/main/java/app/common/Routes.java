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
    
    /*
     * AbstractInstanceController
     */
    public static final String INSTANCES = "/instances";
    public static final String INSTANCES_ZONE_NAME = Routes.INSTANCES + "/{zone}" + "/{name}";
    
    /*
     * AbstractImageController
     */
    public static final String IMAGES = "/images";
}
