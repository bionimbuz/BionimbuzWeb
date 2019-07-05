package models;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.google.gson.annotations.Expose;

import play.data.binding.NoBinding;
import play.db.jpa.GenericModel;

@Entity
@Table(name = "tb_region")
public class RegionModel extends GenericModel {

    @Id
    @GeneratedValue
    private Long id;
    private String name;
    @Expose(serialize = false)
    @NoBinding
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "region")
    private List<InstanceTypeRegionModel> listInstanceTypeRegion;
    @Expose(serialize = false)
    @NoBinding
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "region")
    private List<StorageRegionModel> listStorageRegion;

    public static class Region {

        private Long id;
        private String name;

        public Region(final RegionModel region) {
            this.id = region.getId();
            this.name = region.getName();
        }

        public Region(Long id, String name) {
            this.id = id;
            this.name = name;
        }

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
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Constructors
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    public RegionModel() {
        super();
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Data access
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    public static void deleteForInstancesPriceTable(final Long idPriceTable) {
        delete(
              " DELETE FROM RegionModel region "
            + " WHERE region IN "
            + "     (SELECT region "
            + "         FROM RegionModel region"
            + "         JOIN region.listInstanceTypeRegion instanceTypeRegion"
            + "         WHERE instanceTypeRegion.priceTable.id = ?1)", idPriceTable);
    }
    public static void deleteForStoragesPriceTable(final Long idPriceTable) {
        delete(
                " DELETE FROM RegionModel region "
              + " WHERE region IN "
              + "     (SELECT region "
              + "         FROM RegionModel region"
              + "         JOIN region.listStorageRegion storageRegion"
              + "         WHERE storageRegion.priceTable.id = ?1)", idPriceTable);
    }
    public static List<RegionModel> searchInstanceRegionsForPlugin(final PluginModel plugin) {
        return find(
            " SELECT DISTINCT regions "
            + " FROM InstanceTypeRegionModel instanceTypeRegions"
            + " JOIN instanceTypeRegions.region regions"
            + " WHERE instanceTypeRegions.priceTable.plugin.id = ?1"
            + " ORDER BY regions.name", plugin.getId()).fetch();
    }
    public static List<RegionModel> searchStorageRegionsForPlugin(final PluginModel plugin) {
        return find(
            " SELECT DISTINCT regions "
            + " FROM StorageRegionModel storageRegions"
            + " JOIN storageRegions.region regions"
            + " WHERE storageRegions.priceTable.plugin.id = ?1"
            + " ORDER BY regions.name", plugin.getId()).fetch();
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
    public List<InstanceTypeRegionModel> getListInstanceTypeRegion() {
        return listInstanceTypeRegion;
    }
    public void setListInstanceTypeRegion(
            List<InstanceTypeRegionModel> listInstanceTypeRegion) {
        this.listInstanceTypeRegion = listInstanceTypeRegion;
    }
    public List<StorageRegionModel> getListStorageRegion() {
        return listStorageRegion;
    }
    public void setListStorageRegion(List<StorageRegionModel> listStorageRegion) {
        this.listStorageRegion = listStorageRegion;
    }
}
