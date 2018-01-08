package app.models;

public class InfoModel extends Body {
    
    public static enum AuthenticationType {
        AUTH_BEARER_TOKEN
    }

    private String name;
    private String apiVersion;
    private String pluginVersion;
    private String cloudType;
    private AuthenticationType authType;
    private String writeScope;
    private String readScope;
        
    @SuppressWarnings("unused") //Reflection purposes
    private InfoModel() {    
        super();	
    }
    
    public InfoModel(final String apiVersion) {     
        super();   
        this.apiVersion = apiVersion;
    }
    
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
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
    public AuthenticationType getAuthType() {
        return authType;
    }
    public void setAuthType(final AuthenticationType authType) {
        this.authType = authType;
    }
    public String getWriteScope() {
        return writeScope;
    }
    public void setWriteScope(String writeScope) {
        this.writeScope = writeScope;
    }
    public String getReadScope() {
        return readScope;
    }
    public void setReadScope(String readScope) {
        this.readScope = readScope;
    }
}
