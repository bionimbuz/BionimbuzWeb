package models;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import play.db.jpa.GenericModel;

@Entity
@Table(name = "tb_storage_region")
public class StorageRegionModel extends GenericModel {

    @Id
    @GeneratedValue
    private Long id;
    private Double price;
    private Double classAPrice;
    private Double classBPrice;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private PriceTableModel priceTable;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_region", nullable = false)
    private RegionModel region;

    public static class StorageRegion {
        private Long id;
        private String region;
        private Double price;
        private Double classAPrice;
        private Double classBPrice;

        public StorageRegion(Long id, String region, Double price, Double classAPrice, Double classBPrice) {
            this.id = id;
            this.region = region;
            this.price = price;
            this.classAPrice = classAPrice;
            this.classBPrice = classBPrice;
        }

        public Long getId() {
            return id;
        }
        public void setId(Long id) {
            this.id = id;
        }
        public String getRegion() {
            return region;
        }
        public void setRegion(String region) {
            this.region = region;
        }
        public Double getPrice() {
            return price;
        }
        public void setPrice(Double price) {
            this.price = price;
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
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Constructors
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    public StorageRegionModel() {
        super();
    }
    public StorageRegionModel(RegionModel region) {
        this();
        this.region = region;
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Data access
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    public static void deleteForPriceTable(final Long idPriceTable) {
        delete("priceTable.id = ?1", idPriceTable);
    }

    public static List<StorageRegionModel> searchByPlugin(final Long idPlugin){
        return find("priceTable.plugin.id = ?1 ORDER BY region.name", idPlugin).fetch();
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
    public Double getPrice() {
        return price;
    }
    public void setPrice(Double price) {
        this.price = price;
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
    public PriceTableModel getPriceTable() {
        return priceTable;
    }
    public void setPriceTable(PriceTableModel priceTable) {
        this.priceTable = priceTable;
    }
    public RegionModel getRegion() {
        return region;
    }
    public void setRegion(RegionModel region) {
        this.region = region;
    }
}
