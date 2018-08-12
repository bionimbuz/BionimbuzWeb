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
    
    public final RemoteFileProcessingStatus getDownloadStatus() {
        return downloadStatus;
    }
    public final RemoteFileProcessingStatus getUploadStatus() {
        return uploadStatus;
    }
    public final EXECUTION_PHASE getPhase() {
        return phase;
    }
    public final void setPhase(EXECUTION_PHASE phase) {
        this.phase = phase;
    }
    public final String getErrorMessage() {
        return errorMessage;
    }
    public final void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
    public final boolean isHasError() {
        return hasError;
    }
    public final void setHasError(boolean hasError) {
        this.hasError = hasError;
    }
}