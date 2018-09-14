package models;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import app.models.ExecutionStatus.EXECUTION_PHASE;
import app.models.ExecutionStatus.STATUS;
import play.data.binding.NoBinding;
import play.db.jpa.GenericModel;

@Entity
@Table(name = "tb_workflow_node")
public class WorkflowNodeModel extends GenericModel {

    @Id
    @GeneratedValue
    private Long id;
    @OneToOne
    private InstanceModel instance;
    @NoBinding
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private WorkflowModel workflow;

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Data access
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    public static boolean hasDependentsFinished(final List<Long> nodesIds) {

        final String query = " SELECT COUNT(workflowNode.id)"
                + " FROM WorkflowNodeModel workflowNode "
                + " INNER JOIN workflowNode.instance instance"
                + " WHERE workflowNode.id IN (:nodesIds)"
                + "         AND (instance.status <> ?1 AND instance.status <> ?2)"
                + "         AND (instance.phase <> ?3)";

        final Long count = find(query, STATUS.FINISHED, STATUS.STOPPED, EXECUTION_PHASE.FINISHED)
                .bind("nodesIds", nodesIds)
                .first();
        return count > 0;
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

    public WorkflowModel getWorkflow() {
        return this.workflow;
    }

    public void setWorkflow(final WorkflowModel workflow) {
        this.workflow = workflow;
    }

    public InstanceModel getInstance() {
        return this.instance;
    }

    public void setInstance(final InstanceModel instance) {
        this.instance = instance;
    }
}
