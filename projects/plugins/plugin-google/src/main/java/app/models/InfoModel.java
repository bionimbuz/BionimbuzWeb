package app.models;

public class InfoModel {

    private final String version;
    private final String cloud;
        
    public InfoModel(String version, String cloud) {
        super();
        this.version = version;
        this.cloud = cloud;
    }
    
    public String getVersion() {
        return version;
    }
    public String getCloud() {
        return cloud;
    }    
}
