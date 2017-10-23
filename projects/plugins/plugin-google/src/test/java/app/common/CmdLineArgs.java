package app.common;

public class CmdLineArgs {
    
    private static final String CREDENTIAL_FILE = "credential.file";
    
    public static String getCredentialFile() {
        return System.getProperty(CREDENTIAL_FILE);
    }
}
