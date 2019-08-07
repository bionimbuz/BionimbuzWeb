package models;

import java.util.Date;
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

import play.data.binding.NoBinding;
import play.data.validation.Required;
import play.db.jpa.GenericModel;

@Entity
@Table(name = "tb_space")
public class SpaceModel extends GenericModel {

    @Id
    @GeneratedValue
    private Long id;
    @Required
    private String name;
    @NoBinding
    private Date creationDate;
    @ManyToOne(fetch = FetchType.LAZY)
    @Required
    private PluginModel plugin;
    @NoBinding
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private UserModel user;
    @Required
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private CredentialModel credential;
    @ManyToMany
    @JoinTable(name = "tb_group_space",
    joinColumns = @JoinColumn(name = "id_space", referencedColumnName = "id"),
    inverseJoinColumns = @JoinColumn(name = "id_group", referencedColumnName = "id"))
    private List<GroupModel> listSharedGroups;
    @Required
    private boolean alocationAfterCreation;

    // ---- Redundant Data for price table exclusion/update
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
    public SpaceModel() {
        super();
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
    public String getName() {
        return this.name;
    }
    public void setName(final String name) {
        this.name = name;
    }
    public String getRegionName() {
        return this.regionName;
    }
    public void setRegionName(final String regionName) {
        this.regionName = regionName;
    }
    public Date getPriceTableDate() {
        return this.priceTableDate;
    }
    public void setPriceTableDate(final Date priceTableDate) {
        this.priceTableDate = priceTableDate;
    }
    public Double getPricePerGB() {
        return this.pricePerGB;
    }
    public void setPricePerGB(final Double pricePerGB) {
        this.pricePerGB = pricePerGB;
    }
    public Double getClassAPrice() {
        return this.classAPrice;
    }
    public void setClassAPrice(final Double classAPrice) {
        this.classAPrice = classAPrice;
    }
    public Double getClassBPrice() {
        return this.classBPrice;
    }
    public void setClassBPrice(final Double classBPrice) {
        this.classBPrice = classBPrice;
    }
    public Date getCreationDate() {
        return this.creationDate;
    }
    public void setCreationDate(final Date creationDate) {
        this.creationDate = creationDate;
    }
    public PluginModel getPlugin() {
        return this.plugin;
    }
    public void setPlugin(final PluginModel plugin) {
        this.plugin = plugin;
    }
    public boolean isAlocationAfterCreation() {
        return this.alocationAfterCreation;
    }
    public void setAlocationAfterCreation(final boolean alocationAfterCreation) {
        this.alocationAfterCreation = alocationAfterCreation;
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
    public CredentialModel getCredential() {
        return this.credential;
    }
    public void setCredential(final CredentialModel credential) {
        this.credential = credential;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
