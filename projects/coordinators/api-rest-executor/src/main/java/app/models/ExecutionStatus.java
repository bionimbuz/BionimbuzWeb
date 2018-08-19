package app.models;

public class ExecutionStatus extends Body<ExecutionStatus> {

    public static enum EXECUTION_PHASE{
        STARTING,
        DOWNLOADING,
        EXECUTING,
        UPLOADING,
        FINISHED
    }
    
    private EXECUTION_PHASE phase;
    private RemoteFileProcessingStatus downloadStatus;
    private RemoteFileProcessingStatus uploadStatus;
    private String errorMessage;
    private boolean hasError;
    
    public ExecutionStatus(
            RemoteFileProcessingStatus downloadStatus,
            RemoteFileProcessingStatus uploadStatus) {
        super();
        this.phase = EXECUTION_PHASE.STARTING;
        this.downloadStatus = downloadStatus;
        this.uploadStatus = uploadStatus;
        this.errorMessage = "";
        this.hasError = false;
    }
    
    public RemoteFileProcessingStatus getDownloadStatus() {
        return downloadStatus;
    }
    public RemoteFileProcessingStatus getUploadStatus() {
        return uploadStatus;
    }
    public EXECUTION_PHASE getPhase() {
        return phase;
    }
    public void setPhase(final EXECUTION_PHASE phase) {
        this.phase = phase;
    }
    public String getErrorMessage() {
        return errorMessage;
    }
    public void setErrorMessage(final String errorMessage) {
        this.errorMessage = errorMessage;
    }
    public boolean isHasError() {
        return hasError;
    }
    public void setHasError(boolean hasError) {
        this.hasError = hasError;
    }
}