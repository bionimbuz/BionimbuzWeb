package app.common;

public class GlobalConstants {    

    public static final String API_VERSION = "0.1";
    
    public static final String BNZ_PREFIX = "bionimbuz";
    public static final String BNZ_FIREWALL = BNZ_PREFIX + "-firewall";
    public static final String BNZ_INSTANCE = BNZ_PREFIX + "-instance";
    
    public static final String DEFAULT_NETWORK = "default";
    public static final String DEFAULT_SUBNETWORK = "default";
    public static final String META_STARTUP_SCRIPT = "startup-script";
    
    public static final int TOKEN_LIFETIME_SECONDS = 60 * 5; // 5 Minutes

    public static final String KEYSTONE_HOST = "http://35.199.101.92:5000/v3";
    public static final String HOST = "35.199.101.92";

    public static final String TEST_PROJECT_ID = "42076358faee48b7ae723b06684e54d7";
    public static final String TEST_PROJECT_DOMAIN = "default";
    public static final String TEST_PROJECT_USER = "admin";
    public static final String TEST_PROJECT_PASS = "d7e7a361a0644179";
}
