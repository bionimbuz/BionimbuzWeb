package models;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import play.data.binding.NoBinding;
import play.data.validation.Required;
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
    @Required
    @ManyToOne(fetch = FetchType.LAZY)
    private ExecutorModel executor;
    @NoBinding
    private String cloudInstanceName;
    @NoBinding
    private String cloudInstanceIp;
    @Enumerated(EnumType.STRING)
    @Required
    private CredentialUsagePolicy credentialUsage;
    @ManyToOne(fetch = FetchType.LAZY)
    @Required
    private PluginModel plugin;
    @Required
    private boolean executionAfterCreation;

    // ---- Redundant Data for price table exclusion/update  
    @NoBinding
    private Date priceTableDate;
    @NoBinding
    private Double price;
    @NoBinding
    private String regionName;
    @NoBinding
    private String zoneName;
    @NoBinding
    private String typeName;
    @NoBinding
    private Short cores;
    @NoBinding
    private Double memory;
    // ---------------------------------------
    
    
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
    public String getCloudInstanceIp() {
        return cloudInstanceIp;
    }
    public void setCloudInstanceIp(String cloudInstanceIp) {
        this.cloudInstanceIp = cloudInstanceIp;
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
    public ExecutorModel getExecutor() {
        return executor;
    }
    public void setExecutor(ExecutorModel executor) {
        this.executor = executor;
    }
    public boolean isExecutionAfterCreation() {
        return executionAfterCreation;
    }
    public void setExecutionAfterCreation(boolean executionAfterCreation) {
        this.executionAfterCreation = executionAfterCreation;
    }
    public PluginModel getPlugin() {
        return plugin;
    }
    public void setPlugin(PluginModel plugin) {
        this.plugin = plugin;
    }
    public Date getPriceTableDate() {
        return priceTableDate;
    }
    public void setPriceTableDate(Date priceTableDate) {
        this.priceTableDate = priceTableDate;
    }
    public Double getPrice() {
        return price;
    }
    public void setPrice(Double price) {
        this.price = price;
    }
    public String getTypeName() {
        return typeName;
    }
    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }
    public String getRegionName() {
        return regionName;
    }
    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }
    public String getZoneName() {
        return zoneName;
    }
    public void setZoneName(String zoneName) {
        this.zoneName = zoneName;
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
