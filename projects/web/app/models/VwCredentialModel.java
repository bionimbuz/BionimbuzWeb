package models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import org.hibernate.annotations.Subselect;

import common.fields.EncryptedFileField;
import controllers.adm.BaseAdminController;
import models.InstanceModel.CredentialUsagePolicy;
import play.data.binding.NoBinding;
import play.db.jpa.GenericModel;

@Entity
@Subselect(" SELECT DISTINCT"
        + "      C.id, "
        + "      C.id as id_credential, "
        + "      C.credentialData, "
        + "      C.enabled, "
        + "      C.name,"
        + "      C.plugin_id,"
        + "      C.user_id, "
        + "      (CASE WHEN U.id IS NULL THEN C.user_id ELSE U.id END) AS id_user_shared,"
        + "      GC.id_group IS NOT NULL AS shared,"
        + "      ( U.id IS NULL OR C.user_id = U.id ) userOwner"
        + " FROM tb_credential C"
        + " LEFT JOIN tb_group_credential GC ON ( GC.id_credential = C.id )"
        + " LEFT JOIN tb_group G ON ( G.id = GC.id_group )"
        + " LEFT JOIN tb_user_group UG ON ( UG.id_group = G.id )"
        + " LEFT JOIN tb_user U ON ( U.id = UG.id_user )"
        + "")
public class VwCredentialModel extends GenericModel {

    @NoBinding
    @Id
    private Long id;
    private boolean enabled = true;
    @NoBinding
    private boolean userOwner;
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
    @JoinColumn(name = "id_user_shared", nullable = true)
    private UserModel userShared;
    @NoBinding
    private EncryptedFileField credentialData;
    @Transient
    private List<GroupModel> listSharedGroups;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_credential")
    private CredentialModel credential;

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Constructors
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    public VwCredentialModel() {
        super();
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Data access
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    public static List<VwCredentialModel> searchUserAndPlugin(
            final Long idUser,
            final Long idPlugin,
            final CredentialUsagePolicy credentialUsage) {

        if (credentialUsage == CredentialUsagePolicy.OWNER_FIRST
                || credentialUsage == CredentialUsagePolicy.SHARED_FIRST) {
            final String order = credentialUsage == CredentialUsagePolicy.OWNER_FIRST ? "DESC" : "ASC";
            return find(
                    " SELECT vwCredential "
                            + " FROM VwCredentialModel vwCredential "
                            + " WHERE vwCredential.userShared.id = ?1 "
                            + "       AND vwCredential.plugin.id = ?2 "
                            + " ORDER BY vwCredential.userOwner " + order
                            + "          ,vwCredential.id",
                    idUser, idPlugin).fetch();
        } else if (credentialUsage == CredentialUsagePolicy.ONLY_OWNER
                || credentialUsage == CredentialUsagePolicy.ONLY_SHARED) {
            final boolean onlyOwner = (credentialUsage == CredentialUsagePolicy.ONLY_OWNER);
            return find(
                    " SELECT vwCredential "
                            + " FROM VwCredentialModel vwCredential "
                            + " WHERE vwCredential.userShared.id = ?1 "
                            + "       AND vwCredential.plugin.id = ?2 "
                            + "       AND vwCredential.userOwner = ?3"
                            + " ORDER BY vwCredential.name",
                    idUser, idPlugin, onlyOwner).fetch();
        }
        return new ArrayList<>();
    }

    public static List<VwCredentialModel> searchForCurrentUserAndPlugin(
            final Long idPlugin,
            final CredentialUsagePolicy credentialUsage) {
        final UserModel currentUser = BaseAdminController.getConnectedUser();
        return searchUserAndPlugin(currentUser.getId(), idPlugin, credentialUsage);
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Getters and Setters
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    public Long getId() {
        return this.id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public EncryptedFileField getCredentialData() {
        return this.credentialData;
    }

    public void setCredentialData(final EncryptedFileField credentialData) {
        this.credentialData = credentialData;
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

    public PluginModel getPlugin() {
        return this.plugin;
    }

    public void setPlugin(final PluginModel plugin) {
        this.plugin = plugin;
    }

    public UserModel getUser() {
        return this.user;
    }

    public void setUser(final UserModel user) {
        this.user = user;
    }

    public UserModel getUserShared() {
        return this.userShared;
    }

    public void setUserShared(final UserModel userShared) {
        this.userShared = userShared;
    }

    public boolean isUserOwner() {
        return this.userOwner;
    }

    public void setUserOwner(final boolean owner) {
        this.userOwner = owner;
    }

    public boolean isShared() {
        return this.shared;
    }

    public void setShared(final boolean shared) {
        this.shared = shared;
    }

    public List<GroupModel> getListSharedGroups() {
        return this.listSharedGroups;
    }

    public void setListSharedGroups(final List<GroupModel> listSharedGroups) {
        this.listSharedGroups = listSharedGroups;
    }

    public CredentialModel getCredential() {
        return this.credential;
    }

    public void setCredential(final CredentialModel credential) {
        this.credential = credential;
    }
}
