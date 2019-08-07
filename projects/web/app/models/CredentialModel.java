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
import javax.persistence.OneToMany;
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
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "credential")
    private List<SpaceModel> listSpaces;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "credential")
    private List<InstanceModel> listInstances;

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
        return this.credentialData;
    }
    public void setCredentialData(final EncryptedFileField credentialData) {
        this.credentialData = credentialData;
    }
    public Long getId() {
        return this.id;
    }
    public void setId(final Long id) {
        this.id = id;
    }
    public PluginModel getPlugin() {
        return this.plugin;
    }
    public void setPlugin(final PluginModel plugin) {
        this.plugin = plugin;
    }
    public boolean isEnabled() {
        return this.enabled;
    }
    public void setEnabled(final boolean enabled) {
        this.enabled = enabled;
    }
    public String getName() {
        return this.name;
    }
    public void setName(final String name) {
        this.name = name;
    }
    public String getCredentialDataType() {
        return this.credentialDataType;
    }
    public void setCredentialDataType(final String credentialDataType) {
        this.credentialDataType = credentialDataType;
    }
    public UserModel getUser() {
        return this.user;
    }
    public void setUser(final UserModel user) {
        this.user = user;
    }
    public List<GroupModel> getListSharedGroups() {
        return this.listSharedGroups;
    }
    public void setListSharedGroups(final List<GroupModel> listSharedGroups) {
        this.listSharedGroups = listSharedGroups;
    }
    public List<SpaceModel> getListSpaces() {
        return this.listSpaces;
    }
    public void setListSpaces(final List<SpaceModel> listSpaces) {
        this.listSpaces = listSpaces;
    }
    public List<InstanceModel> getListInstances() {
        return this.listInstances;
    }
    public void setListInstances(final List<InstanceModel> listInstances) {
        this.listInstances = listInstances;
    }

    // ---------------------------------------------------------------------------------------------
    // * @see play.db.jpa.JPABase#toString()
    // ---------------------------------------------------------------------------------------------
    @Override
    public String toString() {
        return this.getName();
    }
}
