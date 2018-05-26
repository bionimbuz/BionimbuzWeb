package app.models;

public class PluginStorageFileUploadModel extends Body {

    private String fileName = "";
    private String spaceName = "";
    private String url = "";
    private String method = "";

    public PluginStorageFileUploadModel() {
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
