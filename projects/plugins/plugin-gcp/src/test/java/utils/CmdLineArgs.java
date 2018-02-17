package utils;

public class CmdLineArgs {

    private static final String CREDENTIAL_FILE = "credential.file";
    private static final String PRICE_TABLE_FILE = "price.table.file";
    
    public static String getCredentialFile() {
        return System.getProperty(CREDENTIAL_FILE);
    }
    public static String getPriceTableFile() {
        return System.getProperty(PRICE_TABLE_FILE);
    }
}
