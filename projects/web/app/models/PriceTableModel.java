package models;

import java.util.Date;
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

import app.models.PluginPriceTableStatusModel;
import play.db.jpa.GenericModel;

@Entity
@Table(name = "tb_price_table")
public class PriceTableModel extends GenericModel {

    public static enum SyncStatus {
        OK,
        PROCESSING,
        ERROR,
    }

    @Id
    @GeneratedValue
    private Long id;
    @OneToOne
    private PluginModel plugin;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "priceTable")
    private List<InstanceTypeRegionModel> listInstanceTypeRegion;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "priceTable")
    private List<StorageRegionModel> listStorageRegion;
    private Date priceTableDate;
    private Date lastSearchDate;
    private Date lastSyncDate;
    @Enumerated(EnumType.STRING)
    private SyncStatus syncStatus;
    private String syncMessage;

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Constructors
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    public PriceTableModel() {
        super();
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Data access
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    public static void deletePriceTable(final Long idPriceTable) {
        StorageRegionModel.deleteForPriceTable(idPriceTable);
        InstanceTypeRegionModel.deleteForPriceTable(idPriceTable);
        RegionModel.deleteForInstancesPriceTable(idPriceTable);
        RegionModel.deleteForStoragesPriceTable(idPriceTable);
        InstanceTypeModel.deleteForPriceTable(idPriceTable);
        delete("id = ?1", idPriceTable);
    }

    public static PriceTableModel findByPluginId(final Long pluginId) {
        return find("plugin.id = ?1", pluginId).first();
    }

    public static SyncStatus getStatus(final PluginPriceTableStatusModel status) {
        switch (status.getStatus()) {
            case OK:
                return SyncStatus.OK;
            case PROCESSING:
                return SyncStatus.PROCESSING;
            case DATE_ERROR:
            case DOWNLOAD_ERROR:
            case PARSE_ERROR:
            case VERSION_ERROR:
            default:
                return SyncStatus.ERROR;
        }
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

    public PluginModel getPlugin() {
        return this.plugin;
    }

    public void setPlugin(final PluginModel plugin) {
        this.plugin = plugin;
    }

    public List<InstanceTypeRegionModel> getListInstanceTypeRegion() {
        return this.listInstanceTypeRegion;
    }

    public void setListInstanceTypeRegion(
            final List<InstanceTypeRegionModel> listInstanceTypeRegion) {
        this.listInstanceTypeRegion = listInstanceTypeRegion;
    }

    public List<StorageRegionModel> getListStorageRegion() {
        return this.listStorageRegion;
    }

    public void setListStorageRegion(final List<StorageRegionModel> listStorageRegion) {
        this.listStorageRegion = listStorageRegion;
    }

    public Date getPriceTableDate() {
        return this.priceTableDate;
    }

    public void setPriceTableDate(final Date priceTableDate) {
        this.priceTableDate = priceTableDate;
    }

    public Date getLastSyncDate() {
        return this.lastSyncDate;
    }

    public void setLastSyncDate(final Date lastSyncDate) {
        this.lastSyncDate = lastSyncDate;
    }

    public Date getLastSearchDate() {
        return this.lastSearchDate;
    }

    public void setLastSearchDate(final Date lastSearchDate) {
        this.lastSearchDate = lastSearchDate;
    }

    public SyncStatus getSyncStatus() {
        return this.syncStatus;
    }

    public void setSyncStatus(final SyncStatus syncStatus) {
        this.syncStatus = syncStatus;
    }

    public String getSyncMessage() {
        return this.syncMessage;
    }

    public void setSyncMessage(final String syncMessage) {
        this.syncMessage = syncMessage;
    }
}
