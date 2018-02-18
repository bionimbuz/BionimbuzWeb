package app.pricing.exceptions;

public class PriceTableVersionException extends Exception {

    private String versionFound;
    private String versionExpected;    
    
    public PriceTableVersionException(
            final String versionFound,
            final String versionExpected) {
        super();
        this.versionFound = versionFound;
        this.versionExpected = versionExpected;
    }

    @Override
    public String getMessage() {        
        return "The price table version found [" + versionFound + "]" 
                + " is different from expected [" + versionExpected + "]"
                + ", the plugin must be updated";
    }
}
