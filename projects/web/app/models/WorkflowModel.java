package models;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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
    @Enumerated(EnumType.STRING)
    private WORKFLOW_STATUS status;
    @NoBinding
    private Date creationDate;
    @Column(length = 3000)
    private String jsonModel;
    @Column(length = 3000)
    private String jsonGraph;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    @NoBinding
    private UserModel user;
    @NoBinding
    private String executionMessage;

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Constructors
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    public WorkflowModel() {
        super();
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Data access
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    public static boolean hasWorkflowFinished(final Long workflowId) {

        final String query = " SELECT COUNT(instance.id)"
                + " FROM InstanceModel instance "
                + " INNER JOIN instance.workflowNode node"
                + " INNER JOIN node.workflow workflow"
                + " WHERE workflow.id = ?1 "
                + "     AND ((instance.status <> ?2 AND instance.status <> ?3) "
                + "     OR (instance.phase <> ?4))";

        final Long count = find(query,
                workflowId,
                STATUS.FINISHED,
                STATUS.STOPPED,
                EXECUTION_PHASE.FINISHED)
                        .first();
        return count == 0;
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

    public String getName() {
        return this.name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public WORKFLOW_STATUS getStatus() {
        return this.status;
    }

    public void setStatus(final WORKFLOW_STATUS status) {
        this.status = status;
    }

    public Date getCreationDate() {
        return this.creationDate;
    }

    public void setCreationDate(final Date creationDate) {
        this.creationDate = creationDate;
    }

    public List<WorkflowNodeModel> getListWorkflowNodes() {
        return this.listWorkflowNodes;
    }

    public void setListWorkflowNodes(final List<WorkflowNodeModel> listWorkflowNodes) {
        this.listWorkflowNodes = listWorkflowNodes;
    }

    public String getJsonModel() {
        return this.jsonModel;
    }

    public void setJsonModel(final String jsonModel) {
        this.jsonModel = jsonModel;
    }

    public String getJsonGraph() {
        return this.jsonGraph;
    }

    public void setJsonGraph(final String jsonGraph) {
        this.jsonGraph = jsonGraph;
    }

    public UserModel getUser() {
        return this.user;
    }

    public void setUser(final UserModel user) {
        this.user = user;
    }

    public String getExecutionMessage() {
        return this.executionMessage;
    }

    public void setExecutionMessage(final String executionMessage) {
        this.executionMessage = executionMessage;
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // * @see play.db.jpa.JPABase#toString()
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    @Override
    public String toString() {
        return this.name;
    }
}
