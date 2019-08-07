package jobs;

import java.util.List;

import app.models.ExecutionStatus;
import app.models.ExecutionStatus.EXECUTION_PHASE;
import app.models.ExecutionStatus.STATUS;
import common.graphs.GraphBidirectional;
import common.graphs.JsonGraph;
import models.InstanceModel;
import models.WorkflowModel;
import models.WorkflowModel.WORKFLOW_STATUS;
import models.WorkflowNodeModel;
import play.jobs.Job;

public class WorkflowExecutionJob extends Job {

    private static final String MSG_NODE_CREATION_ERROR = "A node cannot be created.";
    private static final String MSG_NODE_REMOVE_ERROR = "Some stopped instances cannot be deleted, but workflow execution was proceded.";
    private final Long workflowId;
    private final Long instanceId;
    private final ExecutionStatus instanceStatus;

    // Initial Workflow execution
    public WorkflowExecutionJob(final Long workflowId) {
        super();
        this.instanceId = null;
        this.instanceStatus = null;
        this.workflowId = workflowId;
    }

    // Instance status update
    public WorkflowExecutionJob(
            final Long instanceId,
            final ExecutionStatus instanceStatus) {
        super();
        this.workflowId = null;
        this.instanceId = instanceId;
        this.instanceStatus = instanceStatus;
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // * @see play.jobs.Job#doJob()
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    @Override
    public void doJob() {

        this.startExecution();
    }

    private synchronized void startExecution() {

        if (this.workflowId != null) {

            final WorkflowModel workflow = WorkflowModel.findById(this.workflowId);
            if (workflow == null) {
                return;
            }
            executeNextInstancesFromNode(workflow, JsonGraph.START_OPERATOR_ID);

        } else if (this.instanceId != null && this.instanceStatus != null) {

            final InstanceModel instance = InstanceModel.findById(this.instanceId);
            if (instance == null) {
                return;
            }
            updateNodeStatus(instance, this.instanceStatus);

            if (STATUS.STOPPED != this.instanceStatus.getStatus()
                    && STATUS.FINISHED != this.instanceStatus.getStatus()) {
                return;
            }

            if (instance.getWorkflowNode() != null) {

                final WorkflowModel workflow = instance.getWorkflowNode().getWorkflow();

                if(!InstanceCreationJob.removeCloudInstance(instance, workflow.getUser().getId())) {
                    workflow.setExecutionMessage(MSG_NODE_REMOVE_ERROR);
                    workflow.save();
                }

                // Cannot continues with a workflow already stopped
                if (workflow.getStatus() == WORKFLOW_STATUS.STOPPED
                        || workflow.getStatus() == WORKFLOW_STATUS.FINISHED) {
                    return;
                }

                if (this.instanceStatus.getPhase() != EXECUTION_PHASE.FINISHED) {
                    workflow.setStatus(WORKFLOW_STATUS.STOPPED);
                    workflow.save();
                    return;
                }

                if (WorkflowModel.hasWorkflowFinished(workflow.getId())) {
                    workflow.setStatus(WORKFLOW_STATUS.FINISHED);
                    workflow.save();
                    return;
                }

                executeNextInstancesFromNode(workflow, instance.getWorkflowNode().getId());
            }
        }
    }

    private static void executeNextInstancesFromNode(
            final WorkflowModel workflow,
            final Long fromNodeId) {

        final GraphBidirectional<Long> graph = JsonGraph.getParseGraphBidirectional(workflow.getJsonGraph());
        final List<Long> listStartNodeIds = graph.getNextAdjacentNodes(fromNodeId);
        for (final Long nodeId : listStartNodeIds) {

            final List<Long> listDependents = graph.getDependenciesBackwards(nodeId);
            if (WorkflowNodeModel.hasDependentsNotFinished(listDependents)) {
                continue;
            }

            final InstanceModel instance = InstanceModel.findByWorkflowNodeId(nodeId);
            if (instance == null) {
                continue;
            }

            if (!InstanceCreationJob.executeInstance(
                    instance,
                    workflow.getUser().getId())) {

                workflow.setExecutionMessage(MSG_NODE_CREATION_ERROR);
                workflow.setStatus(WORKFLOW_STATUS.STOPPED);
                break;
            }
            workflow.save();
        }
    }

    private static void updateNodeStatus(
            final InstanceModel instance,
            final ExecutionStatus status) {

        instance.setStatus(status.getStatus());
        instance.setPhase(status.getPhase());
        instance.setExecutionObservation(status.getErrorMessage());
        instance.save();
    }
}
