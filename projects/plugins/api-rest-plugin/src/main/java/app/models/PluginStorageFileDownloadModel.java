package app.models;

public class PluginStorageFileDownloadModel extends Body<PluginStorageFileDownloadModel> {

    private String fileName = "";
    private String spaceName = "";
    private String url = "";
    private String method = "";

    public PluginStorageFileDownloadModel() {
        super();
    }

    public String getFileName() {
        return fileName;
    }
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    public String getSpaceName() {
        return spaceName;
    }
    public void setSpaceName(String spaceName) {
        this.spaceName = spaceName;
    }
    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public String getMethod() {
        return method;
    }
    public void setMethod(String method) {
        this.method = method;
    }
}
