package app.common;

public class Routes {
    /*
     * InfoController
     */
    public static final String INFO = "/info";
    
    /*
     * NetworkController
     */
    public static final String FIREWALLS = "/firewalls";
    public static final String FIREWALLS_NAME = Routes.FIREWALLS+"/{name}";
    
    /*
     * InstanceController
     */
    public static final String INSTANCES = "/instances";
    public static final String INSTANCES_ZONE_NAME = Routes.INSTANCES + "/{zone}" + "/{name}";
}
