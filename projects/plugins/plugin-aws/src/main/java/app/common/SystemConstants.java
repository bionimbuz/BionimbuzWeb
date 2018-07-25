package app.common;

public class SystemConstants {

    public static final String PLUGIN_VERSION = "0.1";
    public static final String PLUGIN_NAME = "Amazon Web Services";

    public static final String CLOUD_COMPUTE_TYPE = "aws-ec2";
    public static final String CLOUD_STORAGE_TYPE = "aws-s3";
    
    public static final String PRICE_TABLE_URL = "https://pricing.us-east-1.amazonaws.com/offers/v1.0/aws/AmazonEC2/current/index.json";
    public static final String PRICE_TABLE_DIR = "pricing/";
    public static final String PRICE_TABLE_FILE = PRICE_TABLE_DIR + "table.json";
    public static final String PRICE_TABLE_VERSION = "v1.0";
}
