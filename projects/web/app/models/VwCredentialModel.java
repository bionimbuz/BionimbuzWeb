package models;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import org.hibernate.annotations.Subselect;

import play.data.binding.NoBinding;
import play.db.jpa.GenericModel;

@Entity
@Subselect(
        " SELECT DISTINCT"
        + "      C.id, "
        + "      C.enabled, "
        + "      C.name,"
        + "      C.plugin_id,"
        + "      C.user_id, "
        + "      ISNULL(U.id, C.user_id) AS id_user_shared,"
        + "      GC.id_group IS NOT NULL AS shared,"
        + "      ( U.id IS NULL OR C.user_id = U.id ) owner"
        + " FROM tb_credential C"
        + " LEFT JOIN tb_group_credential GC ON ( GC.id_credential = C.id )"
        + " LEFT JOIN tb_group G ON ( G.id = GC.id_group )"
        + " LEFT JOIN tb_user_group UG ON ( UG.id_group = G.id )"
        + " LEFT JOIN tb_user U ON ( U.id = UG.id_user )"
        + "")
public class VwCredentialModel extends GenericModel {
    
    @Id
    @NoBinding
    private Long id;
    private boolean enabled = true;
    @NoBinding
    private boolean owner;
    @NoBinding
    private boolean shared;
    private String name;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = true)
    private PluginModel plugin;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    @NoBinding
    private UserModel user;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="id_user_shared", nullable = true)
    private UserModel userShared;
    @Transient
    private List<GroupModel> listSharedGroups;

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Constructors
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~   
    public VwCredentialModel() {
        super();
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
    public PluginModel getPlugin() {
        return plugin;
    }
    public void setPlugin(PluginModel plugin) {
        this.plugin = plugin;
    }
    public UserModel getUser() {
        return user;
    }
    public void setUser(UserModel user) {
        this.user = user;
    }
    public UserModel getUserShared() {
        return userShared;
    }
    public void setUserShared(UserModel userShared) {
        this.userShared = userShared;
    }
    public boolean isOwner() {
        return owner;
    }
    public void setOwner(boolean owner) {
        this.owner = owner;
    }
    public boolean isShared() {
        return shared;
    }
    public void setShared(boolean shared) {
        this.shared = shared;
    }
    public List<GroupModel> getListSharedGroups() {
        return listSharedGroups;
    }
    public void setListSharedGroups(List<GroupModel> listSharedGroups) {
        this.listSharedGroups = listSharedGroups;
    }
}
