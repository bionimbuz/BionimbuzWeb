package models;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import controllers.CRUD.Hidden;
import controllers.adm.BaseAdminController;
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
    @MaxSize(100)
    @Hidden
    private String writeScope;
    @Required
    @MaxSize(100)
    @Hidden
    private String readScope;
    @Required
    @Enumerated(EnumType.STRING)
    private app.models.PluginInfoModel.AuthenticationType authType;    
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "plugin")
    private List<CredentialModel> listCredentials;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "plugin")
    private List<ImageModel> listImages;
    @OneToOne(fetch = FetchType.LAZY, mappedBy="plugin", optional = true)
    private PriceTableModel priceTable;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "plugin")
    private List<VwCredentialModel> listUsersCredentials;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "plugin")
    private List<InstanceModel> listInstances;

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Constructors
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    public PluginModel() {        
        super();
    }
    
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Data access
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    public static List<PluginModel> searchPluginsForUser() {
        UserModel currentUser = BaseAdminController.getConnectedUser();
        return find(
                " SELECT DISTINCT plugins "
                + " FROM PluginModel plugins"
                + " JOIN plugins.listUsersCredentials credentials"
                + " JOIN credentials.userShared userShared"
                + " WHERE userShared.id = ?1", currentUser.getId()).fetch();
    }
    
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Getters and Setters
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
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
    public app.models.PluginInfoModel.AuthenticationType getAuthType() {
        return authType;
    }
    public void setAuthType(app.models.PluginInfoModel.AuthenticationType authType) {
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
    public List<ImageModel> getListImages() {
        return listImages;
    }
    public void setListImages(List<ImageModel> listImages) {
        this.listImages = listImages;
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
    public PriceTableModel getPriceTable() {
        return priceTable;
    }
    public void setPriceTable(PriceTableModel priceTable) {
        this.priceTable = priceTable;
    }
    public List<VwCredentialModel> getListUsersCredentials() {
        return listUsersCredentials;
    }
    public void setListUsersCredentials(
            List<VwCredentialModel> listUsersCredentials) {
        this.listUsersCredentials = listUsersCredentials;
    }
    public List<InstanceModel> getListInstances() {
        return listInstances;
    }
    public void setListInstances(List<InstanceModel> listInstances) {
        this.listInstances = listInstances;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
