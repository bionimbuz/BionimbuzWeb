package models;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import app.models.InfoModel.AuthenticationType;
import play.data.validation.MaxSize;
import play.data.validation.Required;
import play.data.validation.Unique;
import play.db.jpa.GenericModel;

@Entity
@Table(name = "tb_plugin")
public class PluginModel extends GenericModel {

    @Id
    @GeneratedValue
    private Long id;
    private boolean enabled = true;
    @Required
    @MaxSize(100)
    private String url;
    @Required
    @MaxSize(100)
    private String name;
    @Required
    @MaxSize(10)
    private String pluginVersion;
    @Required
    @MaxSize(100)
    @Unique
    private String cloudType;
    @Required
    @Enumerated(EnumType.STRING)
    private AuthenticationType authType;    
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "plugin")
    private List<CredentialModel> listCredentials;
    
    public PluginModel() {        
        super();
    }
    
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getPluginVersion() {
        return pluginVersion;
    }
    public void setPluginVersion(String pluginVersion) {
        this.pluginVersion = pluginVersion;
    }
    public String getCloudType() {
        return cloudType;
    }
    public void setCloudType(String cloudType) {
        this.cloudType = cloudType;
    }
    public AuthenticationType getAuthType() {
        return authType;
    }
    public void setAuthType(AuthenticationType authType) {
        this.authType = authType;
    }
    public boolean isEnabled() {
        return enabled;
    }
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    public List<CredentialModel> getListCredentials() {
        return listCredentials;
    }
    public void setListCredentials(List<CredentialModel> listCredentials) {
        this.listCredentials = listCredentials;
    }     
    
    @Override
    public String toString() {
        return this.name;
    }
}
