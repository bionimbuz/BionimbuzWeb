package models;

import java.util.List;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import models.keys.InstanceTypeZoneKey;
import play.db.jpa.GenericModel;

@Entity
@Table(name = "tb_instance_type_zone")
public class InstanceTypeZoneModel extends GenericModel {

    @EmbeddedId 
    private InstanceTypeZoneKey id;    
    private Double price;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private PriceTableModel priceTable;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_instance_type", nullable = false, insertable=false, updatable=false)
    private InstanceTypeModel instanceType;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_zone", nullable = false, insertable=false, updatable=false)
    private ZoneModel zone;     
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "instanceTypeZone")
    private List<InstanceModel> listInstances;
    
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Constructors
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~    
    public InstanceTypeZoneModel(InstanceTypeModel instanceType, ZoneModel zone) {
        this.instanceType = instanceType;
        this.zone = zone;        
        this.id = new InstanceTypeZoneKey(instanceType.getId(), zone.getId());
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Data access
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~    
    public static void deleteForPriceTable(final Long idPriceTable) {
        delete("priceTable.id = ?1", idPriceTable);
    }
    public static List<InstanceTypeZoneModel> searchForZone(final ZoneModel zone){        
        return find(
                " SELECT instanceTypeZones "
                + " FROM InstanceTypeZoneModel instanceTypeZones"
                + " JOIN FETCH instanceTypeZones.instanceType instanceType"
                + " WHERE instanceTypeZones.zone.id = ?1", zone.getId()).fetch();
    }
    
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Getters and Setters
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    public InstanceTypeZoneKey getId() {
        return id;
    }
    public void setId(InstanceTypeZoneKey id) {
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
    public ZoneModel getZone() {
        return zone;
    }
    public void setZone(ZoneModel zone) {
        this.zone = zone;
    }
    public PriceTableModel getPriceTable() {
        return priceTable;
    }
    public void setPriceTable(PriceTableModel priceTable) {
        this.priceTable = priceTable;
    }
    public List<InstanceModel> getListInstances() {
        return listInstances;
    }
    public void setListInstances(List<InstanceModel> listInstances) {
        this.listInstances = listInstances;
    }    
}
