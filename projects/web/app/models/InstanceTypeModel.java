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

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Constructors
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    public InstanceTypeModel() {
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
