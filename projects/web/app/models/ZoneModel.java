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
@Table(name = "tb_zone")
public class ZoneModel extends GenericModel {

    @Id
    @GeneratedValue
    private Long id;
    private String name;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "zone")
    private List<InstanceTypeZoneModel> listInstanceTypeZone;

    public ZoneModel() {
        super();
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
    public List<InstanceTypeZoneModel> getListInstanceTypeZone() {
        return listInstanceTypeZone;
    }
    public void setListInstanceTypeZone(
            List<InstanceTypeZoneModel> listInstanceTypeZone) {
        this.listInstanceTypeZone = listInstanceTypeZone;
    }
}
