package app.models;

public class InfoModel {

    private String version;
    private String cloud;
        
    @SuppressWarnings("unused") //Reflection purposes
    private InfoModel() {    	
    }
    
    public InfoModel(String version, String cloud) {
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
