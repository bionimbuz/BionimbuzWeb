package models;

import java.util.List;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import models.keys.InstanceTypeRegionKey;
import play.db.jpa.GenericModel;

@Entity
@Table(name = "tb_instance_type_region")
public class InstanceTypeRegionModel extends GenericModel {

    @EmbeddedId 
    private InstanceTypeRegionKey id;    
    private Double price;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private PriceTableModel priceTable;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_instance_type", nullable = false, insertable=false, updatable=false)
    private InstanceTypeModel instanceType;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_region", nullable = false, insertable=false, updatable=false)
    private RegionModel region;     
    
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Constructors
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~    
    public InstanceTypeRegionModel(InstanceTypeModel instanceType, RegionModel region) {
        this.instanceType = instanceType;
        this.region = region;        
        this.id = new InstanceTypeRegionKey(instanceType.getId(), region.getId());
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Data access
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~    
    public static void deleteForPriceTable(final Long idPriceTable) {
        delete("priceTable.id = ?1", idPriceTable);
    }
    public static List<InstanceTypeRegionModel> searchForRegion(final RegionModel region){        
        return find(
                " SELECT instanceTypeRegions "
                + " FROM InstanceTypeRegionModel instanceTypeRegions"
                + " JOIN FETCH instanceTypeRegions.instanceType instanceType"
                + " WHERE instanceTypeRegions.region.id = ?1"
                + " ORDER BY instanceType.name", region.getId()).fetch();
    }
    public static InstanceTypeRegionModel findByInstanceTypeAndRegion(
            final InstanceTypeModel instanceType, final RegionModel region) {
        return findById(new InstanceTypeRegionKey(instanceType, region));        
    }
    
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Getters and Setters
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    public InstanceTypeRegionKey getId() {
        return id;
    }
    public void setId(InstanceTypeRegionKey id) {
        this.id = id;
    }
    public Double getPrice() {
        return price;
    }
    public void setPrice(Double price) {
        this.price = price;
    }
    public InstanceTypeModel getInstanceType() {
        return instanceType;
    }
    public void setInstanceType(InstanceTypeModel instanceType) {
        this.instanceType = instanceType;
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
