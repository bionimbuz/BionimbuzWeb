package app.models;

public class DownloadStatus extends Body {

    private boolean finished;
    private boolean success;

    public DownloadStatus() {
    }

    public DownloadStatus(boolean finished, boolean success) {
        this.finished = finished;
        this.success = success;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

}