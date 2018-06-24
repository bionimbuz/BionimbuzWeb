package models;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import common.binders.FileFieldType;
import common.fields.EncryptedFileField;
import play.data.binding.NoBinding;
import play.data.validation.MaxSize;
import play.data.validation.MinSize;
import play.data.validation.Required;
import play.db.jpa.GenericModel;

@Entity
@Table(name = "tb_credential")
public class CredentialModel extends GenericModel  {

    @Id
    @GeneratedValue
    private Long id;
    private boolean enabled = true;
    @MinSize(3)
    @MaxSize(50)
    @Required
    private String name;
    @FileFieldType("credentialDataType")
    private EncryptedFileField credentialData;
    @NoBinding
    private String credentialDataType;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = true)
    private PluginModel plugin;
    @NoBinding
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private UserModel user;
    @ManyToMany
    @JoinTable(name = "tb_group_credential",
        joinColumns = @JoinColumn(name = "id_credential", referencedColumnName = "id"),
        inverseJoinColumns = @JoinColumn(name = "id_group", referencedColumnName = "id"))
    private List<GroupModel> listSharedGroups;

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Constructors
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    public CredentialModel() {
        super();
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Getters and Setters
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    public EncryptedFileField getCredentialData() {
        return credentialData;
    }
    public void setCredentialData(EncryptedFileField credentialData) {
        this.credentialData = credentialData;
    }
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public PluginModel getPlugin() {
        return plugin;
    }
    public void setPlugin(PluginModel plugin) {
        this.plugin = plugin;
    }
    public boolean isEnabled() {
        return enabled;
    }
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getCredentialDataType() {
        return credentialDataType;
    }
    public void setCredentialDataType(String credentialDataType) {
        this.credentialDataType = credentialDataType;
    }
    public UserModel getUser() {
        return user;
    }
    public void setUser(UserModel user) {
        this.user = user;
    }
    public List<GroupModel> getListSharedGroups() {
        return listSharedGroups;
    }
    public void setListSharedGroups(List<GroupModel> listSharedGroups) {
        this.listSharedGroups = listSharedGroups;
    }
}
