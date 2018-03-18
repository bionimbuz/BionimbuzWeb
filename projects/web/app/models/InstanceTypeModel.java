package models;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import play.db.jpa.GenericModel;

@Entity
@Table(name = "tb_instance_type")
public class InstanceTypeModel extends GenericModel {

    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private Short cores;
    private Double memory;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "instanceType")
    private List<InstanceTypeRegionModel> listInstanceTypeRegion;

    public static class InstanceTypeZone {
        
        private List<InstanceType> listInstanceTypes;
        private List<String> listZones;       
        
        public InstanceTypeZone(
                List<InstanceType> listInstanceTypes,
                List<String> listZones) {
            super();
            this.listInstanceTypes = listInstanceTypes;
            this.listZones = listZones;
        }
        
        public List<InstanceType> getListInstanceTypes() {
            return listInstanceTypes;
        }
        public void setListInstanceTypes(List<InstanceType> listInstanceTypes) {
            this.listInstanceTypes = listInstanceTypes;
        }
        public List<String> getListZones() {
            return listZones;
        }
        public void setListZones(List<String> listZones) {
            this.listZones = listZones;
        }
    }

    public static class InstanceType {
        
        private Long id;
        private String name;
        private Double price;
        private Short cores;
        private Double memory;

        public InstanceType(final InstanceTypeRegionModel instanceTypeRegion) {            
            InstanceTypeModel instanceType = instanceTypeRegion.getInstanceType();            
            this.id = instanceType.getId();
            this.name = instanceType.getName();
            this.price = instanceTypeRegion.getPrice();
            this.cores = instanceType.getCores();
            this.memory = instanceType.getMemory();
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
        public Double getPrice() {
            return price;
        }
        public void setPrice(Double price) {
            this.price = price;
        }
        public Short getCores() {
            return cores;
        }
        public void setCores(Short cores) {
            this.cores = cores;
        }
        public Double getMemory() {
            return memory;
        }
        public void setMemory(Double memory) {
            this.memory = memory;
        }
    }
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Constructors
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    public InstanceTypeModel() {
        super();
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Data access
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~  
    public static void deleteForPriceTable(final Long idPriceTable) {
        delete(
                " DELETE FROM InstanceTypeModel instanceTypeModel "
              + " WHERE instanceTypeModel IN "
              + "     (SELECT instanceTypeModel "
              + "         FROM InstanceTypeModel instanceTypeModel"
              + "         JOIN instanceTypeModel.listInstanceTypeRegion instanceTypeRegion"
              + "         WHERE instanceTypeRegion.priceTable.id = ?1)", idPriceTable);
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
    public Short getCores() {
        return cores;
    }
    public void setCores(Short cores) {
        this.cores = cores;
    }
    public Double getMemory() {
        return memory;
    }
    public void setMemory(Double memory) {
        this.memory = memory;
    }
    public List<InstanceTypeRegionModel> getListInstanceTypeRegion() {
        return listInstanceTypeRegion;
    }
    public void setListInstanceTypeRegion(
            List<InstanceTypeRegionModel> listInstanceTypeRegion) {
        this.listInstanceTypeRegion = listInstanceTypeRegion;
    }
}
