package app.common;

public class Routes {
    /*
     * InfoController
     */
    public static final String INFO = "/info";
    
    /*
     * NetworkController
     */
    public static final String FIREWALL = "/firewall";
    public static final String FIREWALLS = "/firewalls";
    public static final String FIREWALL_NAME = Routes.FIREWALL+"/{name}";
    
    /*
     * InstanceController
     */
    public static final String INSTANCE = "/instance";
    public static final String INSTANCES = "/instances";
    public static final String INSTANCE_ZONE_NAME = Routes.INSTANCE + "/{zone}" + "/{name}";
}
