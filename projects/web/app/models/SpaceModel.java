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
    @ManyToMany
    @JoinTable(name = "tb_group_space",
        joinColumns = @JoinColumn(name = "id_space", referencedColumnName = "id"),
        inverseJoinColumns = @JoinColumn(name = "id_group", referencedColumnName = "id"))
    private List<GroupModel> listSharedGroups;
    @Required
    private boolean alocationAfterCreation;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    @NoBinding
    private CredentialModel credential;

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
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getRegionName() {
        return regionName;
    }
    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }
    public Date getPriceTableDate() {
        return priceTableDate;
    }
    public void setPriceTableDate(Date priceTableDate) {
        this.priceTableDate = priceTableDate;
    }
    public Double getPricePerGB() {
        return pricePerGB;
    }
    public void setPricePerGB(Double pricePerGB) {
        this.pricePerGB = pricePerGB;
    }
    public Double getClassAPrice() {
        return classAPrice;
    }
    public void setClassAPrice(Double classAPrice) {
        this.classAPrice = classAPrice;
    }
    public Double getClassBPrice() {
        return classBPrice;
    }
    public void setClassBPrice(Double classBPrice) {
        this.classBPrice = classBPrice;
    }
    public Date getCreationDate() {
        return creationDate;
    }
    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }
    public PluginModel getPlugin() {
        return plugin;
    }
    public void setPlugin(PluginModel plugin) {
        this.plugin = plugin;
    }
    public boolean isAlocationAfterCreation() {
        return alocationAfterCreation;
    }
    public void setAlocationAfterCreation(boolean alocationAfterCreation) {
        this.alocationAfterCreation = alocationAfterCreation;
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
    public CredentialModel getCredential() {
        return credential;
    }
    public void setCredential(CredentialModel credential) {
        this.credential = credential;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
