package app.models;

import java.util.Date;

public class PluginPriceTableStatusModel {
        
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
    
    public PluginPriceTableStatusModel() {}
    
    private PluginPriceTableStatusModel(
            final Date lastSearch,
            final Status status, 
            final String errorMessage) {
        this.lastSearch = lastSearch;
        this.status = status;
        this.errorMessage = errorMessage;
    }
    
    public static PluginPriceTableStatusModel createOkStatus(final Date lastSearch) {
        return new PluginPriceTableStatusModel(lastSearch, Status.OK, "OK");
    }
    public static PluginPriceTableStatusModel createProcessingStatus() {
        return new PluginPriceTableStatusModel(null, Status.PROCESSING, "The price table is being processed...");
    }
    public static PluginPriceTableStatusModel createDateErrorStatus(final Date lastSearch) {
        return new PluginPriceTableStatusModel(lastSearch, Status.DATE_ERROR, "A valid date cannot be found in price table.");
    }
    public static PluginPriceTableStatusModel createVersionErrorStatus(final Date lastSearch, final String message) {
        return new PluginPriceTableStatusModel(lastSearch, Status.VERSION_ERROR, message);
    }
    public static PluginPriceTableStatusModel createParseErrorStatus(final Date lastSearch, final String message) {
        return new PluginPriceTableStatusModel(lastSearch, Status.PARSE_ERROR, message);
    }
    public static PluginPriceTableStatusModel createDownloadErrorStatus(final Date lastSearch, final String message) {
        return new PluginPriceTableStatusModel(lastSearch, Status.DOWNLOAD_ERROR, message);
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