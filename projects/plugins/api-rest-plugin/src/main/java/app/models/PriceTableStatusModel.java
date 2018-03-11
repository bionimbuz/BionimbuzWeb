package app.models;

import java.util.Date;

public class PriceTableStatusModel {
        
    public static enum Status {
        OK,
        PROCESSING,
        DOWNLOAD_ERROR,
        DATE_ERROR,
        VERSION_ERROR,
        PARSE_ERROR
    }
    
    private Date lastSearch;
    private Status status;
    private String errorMessage;
    
    public PriceTableStatusModel() {}
    
    private PriceTableStatusModel(
            final Date lastSearch,
            final Status status, 
            final String errorMessage) {
        this.lastSearch = lastSearch;
        this.status = status;
        this.errorMessage = errorMessage;
    }
    
    public static PriceTableStatusModel createOkStatus(final Date lastSearch) {
        return new PriceTableStatusModel(lastSearch, Status.OK, "OK");
    }
    public static PriceTableStatusModel createProcessingStatus() {
        return new PriceTableStatusModel(null, Status.PROCESSING, "The price table is being processed...");
    }
    public static PriceTableStatusModel createDateErrorStatus(final Date lastSearch) {
        return new PriceTableStatusModel(lastSearch, Status.DATE_ERROR, "A valid date cannot be found in price table.");
    }
    public static PriceTableStatusModel createVersionErrorStatus(final Date lastSearch, final String message) {
        return new PriceTableStatusModel(lastSearch, Status.VERSION_ERROR, message);
    }
    public static PriceTableStatusModel createParseErrorStatus(final Date lastSearch, final String message) {
        return new PriceTableStatusModel(lastSearch, Status.PARSE_ERROR, message);
    }
    public static PriceTableStatusModel createDownloadErrorStatus(final Date lastSearch, final String message) {
        return new PriceTableStatusModel(lastSearch, Status.DOWNLOAD_ERROR, message);
    }
    
    public Status getStatus() {
        return status;
    }
    public String getErrorMessage() {
        return errorMessage;
    }
    public Date getLastSearch() {
        return lastSearch;
    }
    public void setLastSearch(Date lastSearch) {
        this.lastSearch = lastSearch;
    }
}