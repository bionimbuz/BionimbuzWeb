package models.keys;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import models.InstanceTypeModel;
import models.ZoneModel;

@Embeddable
public class InstanceTypeZoneKey implements Serializable {
    
    @Column(name = "id_instance_type", nullable = false, updatable = false)
    private Long instanceTypeId;
    @Column(name = "id_zone", nullable = false, updatable = false)
    private Long zoneId;

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Constructors
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ 
    public InstanceTypeZoneKey() {
    }
    public InstanceTypeZoneKey(Long instanceTypeId, Long zoneId) {
        this.instanceTypeId = instanceTypeId;
        this.zoneId = zoneId;
    }
    public InstanceTypeZoneKey(InstanceTypeModel instanceType, ZoneModel zone) {
        this(instanceType.getId(), zone.getId());
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Getters and Setters
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    public Long getInstanceTypeId() {
        return instanceTypeId;
    }
    public void setInstanceTypeId(Long instanceTypeId) {
        this.instanceTypeId = instanceTypeId;
    }
    public Long getZoneId() {
        return zoneId;
    }
    public void setZoneId(Long zoneId) {
        this.zoneId = zoneId;
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Overrides
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof InstanceTypeZoneKey)) {
            return false;
        }
        final InstanceTypeZoneKey castOther = (InstanceTypeZoneKey) other;
        return this.instanceTypeId.equals(castOther.instanceTypeId) 
                && this.zoneId.equals(castOther.zoneId);
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int hash = 17;
        hash = hash * prime + this.instanceTypeId.hashCode();
        hash = hash * prime + this.zoneId.hashCode();
        return hash;
    }
}

