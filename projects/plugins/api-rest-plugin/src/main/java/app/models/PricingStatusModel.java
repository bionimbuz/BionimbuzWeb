package app.models;

import java.util.Date;

public class PricingStatusModel {
        
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
    
    private PricingStatusModel(
            final Date lastSearch,
            final Status status, 
            final String errorMessage) {
        this.lastSearch = lastSearch;
        this.status = status;
        this.errorMessage = errorMessage;
    }
    
    public static PricingStatusModel createOkStatus(final Date lastSearch) {
        return new PricingStatusModel(lastSearch, Status.OK, "OK");
    }
    public static PricingStatusModel createProcessingStatus() {
        return new PricingStatusModel(null, Status.PROCESSING, "The price table is being processed...");
    }
    public static PricingStatusModel createDateErrorStatus(final Date lastSearch) {
        return new PricingStatusModel(lastSearch, Status.DATE_ERROR, "A valid date cannot be found in price table.");
    }
    public static PricingStatusModel createVersionErrorStatus(final Date lastSearch, final String message) {
        return new PricingStatusModel(lastSearch, Status.VERSION_ERROR, message);
    }
    public static PricingStatusModel createParseErrorStatus(final Date lastSearch, final String message) {
        return new PricingStatusModel(lastSearch, Status.PARSE_ERROR, message);
    }
    public static PricingStatusModel createDownloadErrorStatus(final Date lastSearch, final String message) {
        return new PricingStatusModel(lastSearch, Status.DOWNLOAD_ERROR, message);
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