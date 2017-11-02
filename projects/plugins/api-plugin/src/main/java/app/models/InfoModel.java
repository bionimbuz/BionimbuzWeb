package app.models;

public class InfoModel {

    private String apiVersion;
    private String pluginVersion;
    private String cloudType;
        
    @SuppressWarnings("unused") //Reflection purposes
    private InfoModel() {    	
    }
    
    public InfoModel(final String apiVersion) {        
        this.apiVersion = apiVersion;
    }
    
    public String getApiVersion() {
        return apiVersion;
    }
    public void setApiVersion(final String apiVersion) {
        this.apiVersion = apiVersion;
    }
    public String getPluginVersion() {
        return pluginVersion;
    }
    public void setPluginVersion(final String pluginVersion) {
        this.pluginVersion = pluginVersion;
    }
    public String getCloudType() {
        return cloudType;
    }
    public void setCloudType(final String cloudType) {
        this.cloudType = cloudType;
    }
}
