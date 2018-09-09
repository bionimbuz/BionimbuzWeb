package models;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import app.models.ExecutionStatus.EXECUTION_PHASE;
import app.models.ExecutionStatus.STATUS;
import play.data.binding.NoBinding;
import play.data.validation.MaxSize;
import play.db.jpa.GenericModel;

@Entity
@Table(name = "tb_workflow")
public class WorkflowModel extends GenericModel {
    
    public static enum WORKFLOW_STATUS {
        EDITING,
        RUNNING,
        STOPPED,
        FINISHED,
    }

    @Id
    @GeneratedValue
    private Long id;
    @MaxSize(100)
    private String name;        
    @NoBinding
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "workflow")
    private List<WorkflowNodeModel> listWorkflowNodes;    
    @NoBinding
    private WORKFLOW_STATUS status;
    @NoBinding
    private Date creationDate;
    @Column(length=3000)
    private String jsonModel;
    @Column(length=3000)
    private String jsonGraph;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)   
    @NoBinding
    private UserModel user;
    @NoBinding
    private String executionMessage;    
        
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Data access
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    public static boolean hasWorkflowFinished(final Long workflowId) {
        return count(  
                " SELECT id"
                + " FROM WorkflowModel workflow "
                + " INNER JOIN workflow.workflowNode workflowNode"
                + " INNER JOIN workflowNode.instance instance"
                + " WHERE workflow.id = ?1 "
                + "         AND (instance.status <> ?2 AND instance.status <> ?3)"
                + "         AND (instance.phase <> ?4)", 
                workflowId, STATUS.FINISHED, STATUS.STOPPED, EXECUTION_PHASE.FINISHED) > 0;
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Constructors
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    public WorkflowModel() {        
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
    public WORKFLOW_STATUS getStatus() {
        return status;
    }
    public void setStatus(WORKFLOW_STATUS status) {
        this.status = status;
    }
    public Date getCreationDate() {
        return creationDate;
    }
    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }
    public List<WorkflowNodeModel> getListWorkflowNodes() {
        return listWorkflowNodes;
    }
    public void setListWorkflowNodes(List<WorkflowNodeModel> listWorkflowNodes) {
        this.listWorkflowNodes = listWorkflowNodes;
    }
    public String getJsonModel() {
        return jsonModel;
    }
    public void setJsonModel(String jsonModel) {
        this.jsonModel = jsonModel;
    }
    public String getJsonGraph() {
        return jsonGraph;
    }
    public void setJsonGraph(String jsonGraph) {
        this.jsonGraph = jsonGraph;
    }
    public UserModel getUser() {
        return user;
    }
    public void setUser(UserModel user) {
        this.user = user;
    }
    public String getExecutionMessage() {
        return executionMessage;
    }   
    public void setExecutionMessage(String executionMessage) {
        this.executionMessage = executionMessage;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
