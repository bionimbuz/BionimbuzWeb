package models;

import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import org.hibernate.annotations.Subselect;

import controllers.adm.BaseAdminController;
import play.data.binding.NoBinding;
import play.db.jpa.GenericModel;

@Entity
@Subselect(" SELECT DISTINCT"
        + "      S.id, "
        + "      S.name,"
        + "      S.creationDate,"
        + "      S.plugin_id,"
        + "      S.user_id,"
        + "      S.regionName,"
        + "      S.priceTableDate,"
        + "      S.pricePerGB,"
        + "      S.classAPrice,"
        + "      S.classBPrice,"
        + "      (CASE WHEN U.id IS NULL THEN S.user_id ELSE U.id END) AS id_user_shared,"
        + "      GS.id_group IS NOT NULL AS shared,"
        + "      ( U.id IS NULL OR S.user_id = U.id ) userOwner"
        + " FROM tb_space S"
        + " LEFT JOIN tb_group_space GS ON ( GS.id_space = S.id )"
        + " LEFT JOIN tb_group G ON ( G.id = GS.id_group )"
        + " LEFT JOIN tb_user_group UG ON ( UG.id_group = G.id )"
        + " LEFT JOIN tb_user U ON ( U.id = UG.id_user )"
        + "")
public class VwSpaceModel extends GenericModel {

    @Id
    @NoBinding
    private Long id;
    @NoBinding
    private String name;
    @NoBinding
    private Date creationDate;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = true)
    private PluginModel plugin;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    @NoBinding
    private UserModel user;
    @Transient
    private List<GroupModel> listSharedGroups;
    @NoBinding
    private boolean userOwner;
    @NoBinding
    private boolean shared;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_user_shared", nullable = true)
    private UserModel userShared;
    @NoBinding
    private String regionName;
    @NoBinding
    private Date priceTableDate;
    @NoBinding
    private Double pricePerGB;
    @NoBinding
    private Double classAPrice;
    @NoBinding
    private Double classBPrice;

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Constructors
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    public VwSpaceModel() {
        super();
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Data access
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    public static List<VwSpaceModel> searchForCurrentUserWithShared() {
        final UserModel currentUser = BaseAdminController.getConnectedUser();
        return find(
                " SELECT vwSpace "
                        + " FROM VwSpaceModel vwSpace "
                        + " WHERE vwSpace.userShared.id = ?1 "
                        + " ORDER BY vwSpace.name",
                currentUser.getId()).fetch();
    }

    public static List<VwSpaceModel> searchForCurrentUserAndPlugin(
            final Long idPlugin) {
        final UserModel currentUser = BaseAdminController.getConnectedUser();
        return find(
                " SELECT vwSpace "
                        + " FROM VwSpaceModel vwSpace "
                        + " WHERE vwSpace.userShared.id = ?1 "
                        + "       AND vwSpace.plugin.id = ?2 "
                        + "       AND vwSpace.userOwner = TRUE"
                        + " ORDER BY vwSpace.name",
                currentUser.getId(), idPlugin).fetch();
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Getters and Setters
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    public final Long getId() {
        return this.id;
    }

    public final void setId(final Long id) {
        this.id = id;
    }

    public final boolean isUserOwner() {
        return this.userOwner;
    }

    public final void setUserOwner(final boolean owner) {
        this.userOwner = owner;
    }

    public final boolean isShared() {
        return this.shared;
    }

    public final void setShared(final boolean shared) {
        this.shared = shared;
    }

    public final String getName() {
        return this.name;
    }

    public final void setName(final String name) {
        this.name = name;
    }

    public final Date getCreationDate() {
        return this.creationDate;
    }

    public final void setCreationDate(final Date creationDate) {
        this.creationDate = creationDate;
    }

    public final PluginModel getPlugin() {
        return this.plugin;
    }

    public final void setPlugin(final PluginModel plugin) {
        this.plugin = plugin;
    }

    public final UserModel getUser() {
        return this.user;
    }

    public final void setUser(final UserModel user) {
        this.user = user;
    }

    public final List<GroupModel> getListSharedGroups() {
        return this.listSharedGroups;
    }

    public final void setListSharedGroups(final List<GroupModel> listSharedGroups) {
        this.listSharedGroups = listSharedGroups;
    }

    public final String getRegionName() {
        return this.regionName;
    }

    public final void setRegionName(final String regionName) {
        this.regionName = regionName;
    }

    public final Date getPriceTableDate() {
        return this.priceTableDate;
    }

    public final void setPriceTableDate(final Date priceTableDate) {
        this.priceTableDate = priceTableDate;
    }

    public final Double getPricePerGB() {
        return this.pricePerGB;
    }

    public final void setPricePerGB(final Double pricePerGB) {
        this.pricePerGB = pricePerGB;
    }

    public final Double getClassAPrice() {
        return this.classAPrice;
    }

    public final void setClassAPrice(final Double classAPrice) {
        this.classAPrice = classAPrice;
    }

    public final Double getClassBPrice() {
        return this.classBPrice;
    }

    public final void setClassBPrice(final Double classBPrice) {
        this.classBPrice = classBPrice;
    }

    public final UserModel getUserShared() {
        return this.userShared;
    }

    public final void setUserShared(final UserModel userShared) {
        this.userShared = userShared;
    }
}
