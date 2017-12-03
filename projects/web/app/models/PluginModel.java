package models;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import app.models.InfoModel.AuthenticationType;
import play.data.binding.NoBinding;
import play.data.validation.MaxSize;
import play.data.validation.Required;
import play.db.jpa.GenericModel;

@Entity
@Table(name = "tb_plugin")
public class PluginModel extends GenericModel {

    @Id
    @GeneratedValue
    private Long id;
    private boolean enabled = true;
    @Required
    @MaxSize(400)
    private String url;
    @Required
    @MaxSize(100)
    @NoBinding
    private String name;
    @MaxSize(10)
    private String pluginVersion;
    @MaxSize(100)
    private String cloudType;
    @Enumerated(EnumType.STRING)
    private AuthenticationType authType;    
    
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
}
