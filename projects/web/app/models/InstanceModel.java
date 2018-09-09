package models;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import app.models.ExecutionStatus.EXECUTION_PHASE;
import app.models.ExecutionStatus.STATUS;
import play.data.binding.NoBinding;
import play.data.validation.Required;
import play.data.validation.Unique;
import play.db.jpa.GenericModel;

@Entity
@Table(name = "tb_instance")
public class InstanceModel extends GenericModel {

    public static enum CredentialUsagePolicy {
        SHARED_FIRST,
        OWNER_FIRST,
        ONLY_SHARED,
        ONLY_OWNER
    }
    
    public static enum StatusPresentation{
        NORMAL,
        EXECUTING,
        SUCCESS,
        ERROR
    }    

    @Id
    @GeneratedValue
    private Long id;
    @Unique
    @NoBinding
    private String instanceIdentity;
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
    @NoBinding
    private Date creationDate;
    @OneToOne(cascade = {
            CascadeType.PERSIST,
            CascadeType.REMOVE
    }, orphanRemoval = true)
    private ApplicationArgumentsModel applicationArguments;
    @NoBinding
    @Enumerated(EnumType.STRING)
    private EXECUTION_PHASE phase;
    @NoBinding
    @Enumerated(EnumType.STRING)
    private STATUS status;
    @NoBinding
    private String executionObservation;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = true)
    private CredentialModel credential;
    @NoBinding
    @OneToOne(fetch = FetchType.LAZY, mappedBy = "instance", optional = true)
    private WorkflowNodeModel workflowNode;

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
    // Data access
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    public static InstanceModel findByIdentity(final String identity) {
        return find("instanceIdentity = ?1", identity).first();
    }
    public static InstanceModel findByWorkflowNodeId(final Long nodeId) {
        return find("workflowNode.id = ?1", nodeId).first();
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Getters and Setters
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    public Long getId() {
        return this.id;
    }
    public void setId(final Long id) {
        this.id = id;
    }
    public String getCloudInstanceIp() {
        return this.cloudInstanceIp;
    }
    public void setCloudInstanceIp(final String cloudInstanceIp) {
        this.cloudInstanceIp = cloudInstanceIp;
    }
    public String getCloudInstanceName() {
        return this.cloudInstanceName;
    }
    public void setCloudInstanceName(final String cloudInstanceName) {
        this.cloudInstanceName = cloudInstanceName;
    }
    public CredentialUsagePolicy getCredentialUsage() {
        return this.credentialUsage;
    }
    public void setCredentialUsage(final CredentialUsagePolicy credentialUsage) {
        this.credentialUsage = credentialUsage;
    }
    public ExecutorModel getExecutor() {
        return this.executor;
    }
    public void setExecutor(final ExecutorModel executor) {
        this.executor = executor;
    }
    public boolean isExecutionAfterCreation() {
        return this.executionAfterCreation;
    }
    public void setExecutionAfterCreation(final boolean executionAfterCreation) {
        this.executionAfterCreation = executionAfterCreation;
    }
    public PluginModel getPlugin() {
        return this.plugin;
    }
    public void setPlugin(final PluginModel plugin) {
        this.plugin = plugin;
    }
    public Date getPriceTableDate() {
        return this.priceTableDate;
    }
    public void setPriceTableDate(final Date priceTableDate) {
        this.priceTableDate = priceTableDate;
    }
    public Double getPrice() {
        return this.price;
    }
    public void setPrice(final Double price) {
        this.price = price;
    }
    public String getTypeName() {
        return this.typeName;
    }
    public void setTypeName(final String typeName) {
        this.typeName = typeName;
    }
    public String getRegionName() {
        return this.regionName;
    }
    public void setRegionName(final String regionName) {
        this.regionName = regionName;
    }
    public String getZoneName() {
        return this.zoneName;
    }
    public void setZoneName(final String zoneName) {
        this.zoneName = zoneName;
    }
    public Short getCores() {
        return this.cores;
    }
    public void setCores(final Short cores) {
        this.cores = cores;
    }
    public Double getMemory() {
        return this.memory;
    }
    public void setMemory(final Double memory) {
        this.memory = memory;
    }
    public Date getCreationDate() {
        return this.creationDate;
    }
    public void setCreationDate(final Date creationDate) {
        this.creationDate = creationDate;
    }
    public String getInstanceIdentity() {
        return this.instanceIdentity;
    }
    public void setInstanceIdentity(final String instanceIdentity) {
        this.instanceIdentity = instanceIdentity;
    }
    public ApplicationArgumentsModel getApplicationArguments() {
        return this.applicationArguments;
    }
    public void setApplicationArguments(
            final ApplicationArgumentsModel applicationArguments) {
        this.applicationArguments = applicationArguments;
    }
    public EXECUTION_PHASE getPhase() {
        return this.phase;
    }
    public void setPhase(final EXECUTION_PHASE phase) {
        this.phase = phase;
    }
    public STATUS getStatus() {
        return this.status;
    }
    public void setStatus(final STATUS status) {
        this.status = status;
    }
    public String getExecutionObservation() {
        return this.executionObservation;
    }
    public void setExecutionObservation(final String executionObservation) {
        this.executionObservation = executionObservation;
    }
    public CredentialModel getCredential() {
        return this.credential;
    }
    public void setCredential(final CredentialModel credential) {
        this.credential = credential;
    }
    public WorkflowNodeModel getWorkflowNode() {
        return this.workflowNode;
    }
    public void setWorkflowNode(final WorkflowNodeModel workflowNode) {
        this.workflowNode = workflowNode;
    }

    @Transient
    public StatusPresentation getStatusPresentation() {        
        if(status == STATUS.STOPPED || status == STATUS.FINISHED) {
            if(phase == EXECUTION_PHASE.FINISHED)
                return StatusPresentation.SUCCESS;
            return StatusPresentation.ERROR; 
        } else if(status == STATUS.RUNNING) {
            return StatusPresentation.EXECUTING;
        }        
        return StatusPresentation.NORMAL;
    }
}
