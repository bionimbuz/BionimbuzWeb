package app.models;

public class PluginInfoModel extends Body {

    public static enum AuthenticationType {
        AUTH_SUPER_USER,
        AUTH_BEARER_TOKEN,
        AUTH_AWS
    }

    private String name;
    private String apiVersion;
    private String pluginVersion;
    private String cloudType;
    private AuthenticationType authType;
    private String instanceWriteScope;
    private String instanceReadScope;
    private String storageWriteScope;
    private String storageReadScope;

    @SuppressWarnings("unused") //Reflection purposes
    private PluginInfoModel() {
        super();
    }

    public PluginInfoModel(final String apiVersion) {
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
    public String getInstanceWriteScope() {
        return instanceWriteScope;
    }
    public void setInstanceWriteScope(String instanceWriteScope) {
        this.instanceWriteScope = instanceWriteScope;
    }
    public String getInstanceReadScope() {
        return instanceReadScope;
    }
    public void setInstanceReadScope(String instanceReadScope) {
        this.instanceReadScope = instanceReadScope;
    }
    public String getStorageWriteScope() {
        return storageWriteScope;
    }
    public void setStorageWriteScope(String storageWriteScope) {
        this.storageWriteScope = storageWriteScope;
    }
    public String getStorageReadScope() {
        return storageReadScope;
    }
    public void setStorageReadScope(String storageReadScope) {
        this.storageReadScope = storageReadScope;
    }
}
