package models;

import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import play.db.jpa.GenericModel;

@Entity
@Table(name = "tb_price_table")
public class PriceTableModel extends GenericModel {

    @Id
    @GeneratedValue
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private PluginModel plugin;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "instanceType")
    private List<InstanceTypeZoneModel> listInstanceTypeZone;
    private Date priceTableDate;
    private Date lastSearchDate;
    private Date lastSyncDate;

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Constructors
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    public PriceTableModel() {
        super();
    }
    
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Data access
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    public static PriceTableModel getRecentPriceTable(final Long pluginId) {
        return find(
                  " SELECT priceTable "
                + " FROM PriceTableModel priceTable"
                + " WHERE plugin.id = ?1"
                + " ORDER BY priceTableDate DESC", pluginId).first();        
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
    public PluginModel getPlugin() {
        return plugin;
    }
    public void setPlugin(PluginModel plugin) {
        this.plugin = plugin;
    }
    public List<InstanceTypeZoneModel> getListInstanceTypeZone() {
        return listInstanceTypeZone;
    }
    public void setListInstanceTypeZone(
            List<InstanceTypeZoneModel> listInstanceTypeZone) {
        this.listInstanceTypeZone = listInstanceTypeZone;
    }
    public Date getPriceTableDate() {
        return priceTableDate;
    }
    public void setPriceTableDate(Date priceTableDate) {
        this.priceTableDate = priceTableDate;
    }
    public Date getLastSyncDate() {
        return lastSyncDate;
    }
    public void setLastSyncDate(Date lastSyncDate) {
        this.lastSyncDate = lastSyncDate;
    }
    public Date getLastSearchDate() {
        return lastSearchDate;
    }
    public void setLastSearchDate(Date lastSearchDate) {
        this.lastSearchDate = lastSearchDate;
    }   
}
