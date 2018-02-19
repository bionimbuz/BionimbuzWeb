package models;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import play.db.jpa.GenericModel;

@Entity
@Table(name = "tb_instance")
public class InstanceModel extends GenericModel {

    public static enum CredentialUsagePolicy {
        SHARED_FIRST,
        OWNER_FIRST
    }
    
    @Id
    @GeneratedValue
    private Long id;
    private String cloudInstanceName;
    @Enumerated(EnumType.STRING)
    private CredentialUsagePolicy credentialUsage;
    @ManyToOne
    @JoinColumns({
        @JoinColumn(
            nullable = false,
            name = "id_instance_type",
            referencedColumnName = "id_instance_type"),
        @JoinColumn(
            nullable = false,
            name = "id_zone",
            referencedColumnName = "id_zone")
    })
    private InstanceTypeZoneModel instanceTypeZone;
    
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Constructors
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    public InstanceModel() {
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
    public String getCloudInstanceName() {
        return cloudInstanceName;
    }
    public void setCloudInstanceName(String cloudInstanceName) {
        this.cloudInstanceName = cloudInstanceName;
    }
    public CredentialUsagePolicy getCredentialUsage() {
        return credentialUsage;
    }
    public void setCredentialUsage(CredentialUsagePolicy credentialUsage) {
        this.credentialUsage = credentialUsage;
    }
    public InstanceTypeZoneModel getInstanceTypeZone() {
        return instanceTypeZone;
    }
    public void setInstanceTypeZone(InstanceTypeZoneModel instanceTypeZone) {
        this.instanceTypeZone = instanceTypeZone;
    }
}
