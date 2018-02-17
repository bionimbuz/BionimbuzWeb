package app.models.pricing;

import java.util.Date;

public class StatusPricing {
        
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
    
    private StatusPricing(
            final Date lastSearch,
            final Status status, 
            final String errorMessage) {
        this.lastSearch = lastSearch;
        this.status = status;
        this.errorMessage = errorMessage;
    }
    
    public static StatusPricing createOkStatus(final Date lastSearch) {
        return new StatusPricing(lastSearch, Status.OK, "OK");
    }
    public static StatusPricing createProcessingStatus() {
        return new StatusPricing(null, Status.PROCESSING, "The price table is being processed...");
    }
    public static StatusPricing createDateErrorStatus(final Date lastSearch) {
        return new StatusPricing(lastSearch, Status.DATE_ERROR, "A valid date cannot be found in price table.");
    }
    public static StatusPricing createVersionErrorStatus(final Date lastSearch, final String message) {
        return new StatusPricing(lastSearch, Status.VERSION_ERROR, message);
    }
    public static StatusPricing createParseErrorStatus(final Date lastSearch, final String message) {
        return new StatusPricing(lastSearch, Status.PARSE_ERROR, message);
    }
    public static StatusPricing createDownloadErrorStatus(final Date lastSearch, final String message) {
        return new StatusPricing(lastSearch, Status.DOWNLOAD_ERROR, message);
    }
    
    public Status getStatus() {
        return status;
    }
    public void setStatus(final Status status) {
        this.status = status;
    }
    public String getErrorMessage() {
        return errorMessage;
    }
    public void setErrorMessage(final String errorMessage) {
        this.errorMessage = errorMessage;
    }
    public Date getLastSearch() {
        return lastSearch;
    }
    public void setLastSearch(Date lastSearch) {
        this.lastSearch = lastSearch;
    }
}