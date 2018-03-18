package models.keys;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import models.InstanceTypeModel;
import models.RegionModel;

@Embeddable
public class InstanceTypeRegionKey implements Serializable {
    
    @Column(name = "id_instance_type", nullable = false, updatable = false)
    private Long instanceTypeId;
    @Column(name = "id_region", nullable = false, updatable = false)
    private Long regionId;

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Constructors
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ 
    public InstanceTypeRegionKey() {
    }
    public InstanceTypeRegionKey(Long instanceTypeId, Long regionId) {
        this.instanceTypeId = instanceTypeId;
        this.regionId = regionId;
    }
    public InstanceTypeRegionKey(InstanceTypeModel instanceType, RegionModel region) {
        this(instanceType.getId(), region.getId());
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
    public Long getRegionId() {
        return regionId;
    }
    public void setRegionId(Long regionId) {
        this.regionId = regionId;
    }
    
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Overrides
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof InstanceTypeRegionKey)) {
            return false;
        }
        final InstanceTypeRegionKey castOther = (InstanceTypeRegionKey) other;
        return this.instanceTypeId.equals(castOther.instanceTypeId) 
                && this.regionId.equals(castOther.regionId);
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int hash = 17;
        hash = hash * prime + this.instanceTypeId.hashCode();
        hash = hash * prime + this.regionId.hashCode();
        return hash;
    }
}

