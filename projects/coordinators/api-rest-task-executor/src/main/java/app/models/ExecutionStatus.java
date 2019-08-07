package app.models;

public class ExecutionStatus extends Body<ExecutionStatus> {

    public static enum STATUS {
        IDDLE,
        RUNNING,
        STOPPED,
        FINISHED
    }

    public static enum EXECUTION_PHASE {
        WAITING,
        STARTING,
        DOWNLOADING,
        EXECUTING,
        UPLOADING,
        FINISHED,
        ERROR
    }

    private STATUS status;
    private EXECUTION_PHASE phase;
    private RemoteFileProcessingStatus downloadStatus;
    private RemoteFileProcessingStatus uploadStatus;
    private String errorMessage;
    private boolean hasError;

    public ExecutionStatus() {
        super();
    }
    public ExecutionStatus(
            final RemoteFileProcessingStatus downloadStatus,
            final RemoteFileProcessingStatus uploadStatus) {
        super();
        this.phase = EXECUTION_PHASE.WAITING;
        this.downloadStatus = downloadStatus;
        this.uploadStatus = uploadStatus;
        this.errorMessage = "";
        this.hasError = false;
    }

    public RemoteFileProcessingStatus getDownloadStatus() {
        return this.downloadStatus;
    }
    public RemoteFileProcessingStatus getUploadStatus() {
        return this.uploadStatus;
    }
    public EXECUTION_PHASE getPhase() {
        return this.phase;
    }
    public void setPhase(final EXECUTION_PHASE phase) {
        this.phase = phase;
    }
    public String getErrorMessage() {
        return this.errorMessage;
    }
    public void setErrorMessage(final String errorMessage) {
        this.errorMessage = errorMessage;
    }
    public boolean isHasError() {
        return this.hasError;
    }
    public void setHasError(final boolean hasError) {
        this.hasError = hasError;
    }
    public STATUS getStatus() {
        return this.status;
    }
    public void setStatus(final STATUS status) {
        this.status = status;
    }
}