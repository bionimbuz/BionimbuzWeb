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
@Table(name = "tb_zone")
public class ZoneModel extends GenericModel {

    @Id
    @GeneratedValue
    private Long id;
    private String name;
    @Expose(serialize = false)
    @NoBinding
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "zone")
    private List<InstanceTypeZoneModel> listInstanceTypeZone;
    
    public static class Zone {

        private Long id;
        private String name;
        
        public Zone(final ZoneModel zone) {
            this.id = zone.getId();
            this.name = zone.getName();
        }
        
        public Zone(Long id, String name) {
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
    public ZoneModel() {
        super();
    }
    
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Data access
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~  
    public static void deleteForPriceTable(final Long idPriceTable) {
        delete(
              " DELETE FROM ZoneModel zone "
            + " WHERE zone IN "
            + "     (SELECT zone "
            + "         FROM ZoneModel zone"
            + "         JOIN zone.listInstanceTypeZone instanceTypeZone"
            + "         WHERE instanceTypeZone.priceTable.id = ?1)", idPriceTable);
    }
    public static List<ZoneModel> searchZonesForPlugin(final PluginModel plugin) {
        return find(
            " SELECT DISTINCT zones "
            + " FROM InstanceTypeZoneModel instanceTypeZones"
            + " JOIN instanceTypeZones.zone zones"
            + " WHERE instanceTypeZones.priceTable.plugin.id = ?1", plugin.getId()).fetch();
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
    public List<InstanceTypeZoneModel> getListInstanceTypeZone() {
        return listInstanceTypeZone;
    }
    public void setListInstanceTypeZone(
            List<InstanceTypeZoneModel> listInstanceTypeZone) {
        this.listInstanceTypeZone = listInstanceTypeZone;
    }
}
