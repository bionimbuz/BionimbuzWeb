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

    private static final String MSG_NODE_EXECUTION_ERROR = "A node has an execution problem.";
    private static final String MSG_NODE_CREATION_ERROR = "A node cannot be created.";
    private Long workflowId = null;
    private Long instanceId = null;
    private ExecutionStatus status = null;
    private boolean startingCompleteWorkflow;

    // Initial Workflow execution
    public WorkflowExecutionJob(final Long workflowId) {
        this.workflowId = workflowId;
        this.startingCompleteWorkflow = true;
    }

    // Instance status update
    public WorkflowExecutionJob(
            final Long instanceId,
            final ExecutionStatus instanceStatus) {
        super();
        this.instanceId = instanceId;
        this.status = instanceStatus;
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // * @see play.jobs.Job#doJob()
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    @Override
    public void doJob() {

        if (this.startingCompleteWorkflow) {
            this.startCompleteExecution();
        } else {
            this.updateExecutionStatus();
        }
    }

    private void updateExecutionStatus() {

        final InstanceModel instance = InstanceModel.findById(this.instanceId);
        updateStatus(instance, this.status);
    }

    private synchronized void startCompleteExecution() {

        if (this.workflowId != null) {
            startExecution(this.workflowId);
        } else if (this.instanceId != null && this.status != null) {
            final InstanceModel instance = InstanceModel.findById(this.instanceId);
            updateStatus(instance, this.status);
            if (this.status.getStatus() != STATUS.STOPPED
                    && this.status.getStatus() != STATUS.FINISHED) {
                return;
            }
            final WorkflowModel workflow = instance.getWorkflowNode().getWorkflow();

            if (this.status.getPhase() != EXECUTION_PHASE.FINISHED) {
                workflow.setExecutionMessage(MSG_NODE_EXECUTION_ERROR);
                workflow.setStatus(WORKFLOW_STATUS.STOPPED);
                workflow.save();
                return;
            }

            if (WorkflowModel.hasWorkflowFinished(workflow.getId())) {
                workflow.setStatus(WORKFLOW_STATUS.FINISHED);
                workflow.save();
                return;
            }

            // Cannot continues with a workflow already stopped
            if (workflow.getStatus() == WORKFLOW_STATUS.STOPPED
                    || workflow.getStatus() == WORKFLOW_STATUS.FINISHED) {
                return;
            }

            final GraphBidirectional<Long> graph = JsonGraph.getParseGraphBidirectional(
                    workflow.getJsonGraph());
            executeNextInstancesFromNode(
                    workflow,
                    graph,
                    instance.getWorkflowNode().getId());
        }
    }

    private static void startExecution(final Long workflowId) {

        final WorkflowModel workflow = WorkflowModel.findById(workflowId);
        if (workflow == null) {
            return;
        }

        final GraphBidirectional<Long> graph = JsonGraph.getParseGraphBidirectional(workflow.getJsonGraph());
        executeNextInstancesFromNode(
                workflow,
                graph,
                JsonGraph.START_OPERATOR_ID);
    }

    private static void executeNextInstancesFromNode(
            final WorkflowModel workflow,
            final GraphBidirectional<Long> graph,
            final Long fromNodeId) {

        final List<Long> listStartNodeIds = graph.getNextAdjacentNodes(fromNodeId);
        for (final Long nodeId : listStartNodeIds) {
            final List<Long> listDependents = graph.getDependenciesBackwards(nodeId);
            if (!WorkflowNodeModel.hasDependentsFinished(listDependents)) {
                continue;
            }
            final InstanceModel instance = InstanceModel.findByWorkflowNodeId(nodeId);
            if (instance == null) {
                continue;
            }
            if (!InstanceCreationJob.executeInstance(
                    instance.getId(),
                    workflow.getUser().getId())) {

                workflow.setExecutionMessage(MSG_NODE_CREATION_ERROR);
                workflow.setStatus(WORKFLOW_STATUS.STOPPED);
                workflow.save();
                return;
            }
        }
    }

    private static void updateStatus(
            final InstanceModel instance,
            final ExecutionStatus status) {
        instance.setStatus(status.getStatus());
        instance.setPhase(status.getPhase());
        instance.setExecutionObservation(status.getErrorMessage());
        instance.save();
    }
}
