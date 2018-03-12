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
    private List<InstanceTypeZoneModel> listInstanceTypeZone;


    public static class InstanceType {
        
        private Long id;
        private String name;
        private Double price;
        private Short cores;
        private Double memory;
        

        public InstanceType(final InstanceTypeZoneModel instanceTypeZone) {            
            InstanceTypeModel instanceType = instanceTypeZone.getInstanceType();            
            this.id = instanceType.getId();
            this.name = instanceType.getName();
            this.price = instanceTypeZone.getPrice();
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
              + "         JOIN instanceTypeModel.listInstanceTypeZone instanceTypeZone"
              + "         WHERE instanceTypeZone.priceTable.id = ?1)", idPriceTable);
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
    public List<InstanceTypeZoneModel> getListInstanceTypeZone() {
        return listInstanceTypeZone;
    }
    public void setListInstanceTypeZone(
            List<InstanceTypeZoneModel> listInstanceTypeZone) {
        this.listInstanceTypeZone = listInstanceTypeZone;
    }
}
