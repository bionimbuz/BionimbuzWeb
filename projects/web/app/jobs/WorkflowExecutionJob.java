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
    private static final String MSG_GRAPH_ERROR = "Error on nodes connections.";
    private Long workflowId = null;
    private Long instanceId = null;
    private ExecutionStatus status = null;    
    
    // Initial Workflow execution
    public WorkflowExecutionJob(final Long workflowId) {
        this.workflowId = workflowId;
    }
    
    // Instance status update
    public WorkflowExecutionJob(
            Long instanceId,
            ExecutionStatus instanceStatus) {
        super();
        this.instanceId = instanceId;
        this.status = instanceStatus;
    }

    @Override
    public void doJob() {
        if(!proccessNeedSynchronization())
            return;
        processRequisition();
    }
    
    private boolean proccessNeedSynchronization() {
        if(this.instanceId != null) {
            return false;
        }        
        InstanceModel instance = 
                InstanceModel.findById(this.instanceId);
        if(instance == null) {
            return false;
        }            
        if(instance.getWorkflowNode() != null) {     
            return true;
        }        
        updateStatus(instance, status);
        return false;
    }

    private synchronized void processRequisition() {
        if(workflowId != null) {
            startExecution(workflowId);
        } else if (this.instanceId != null && status != null) {
            InstanceModel instance = 
                    InstanceModel.findById(this.instanceId);
            updateStatus(instance, status);            
            if(status.getStatus() != STATUS.STOPPED 
                    && status.getStatus() != STATUS.FINISHED) {
                return;
            }
            WorkflowModel workflow = 
                    instance.getWorkflowNode().getWorkflow();
            
            if(status.getPhase() != EXECUTION_PHASE.FINISHED) {
                workflow.setExecutionMessage(MSG_NODE_EXECUTION_ERROR);
                workflow.setStatus(WORKFLOW_STATUS.STOPPED);
                workflow.save();
                return;
            }

            if(WorkflowModel.hasWorkflowFinished(workflow.getId())) {
                workflow.setStatus(WORKFLOW_STATUS.FINISHED);
                workflow.save();
                return;
            }
            
            // Cannot continues with a workflow already stopped
            if(workflow.getStatus() == WORKFLOW_STATUS.STOPPED
                || workflow.getStatus() == WORKFLOW_STATUS.FINISHED) {
                return;
            }
            
            GraphBidirectional<Long> graph = 
                    JsonGraph.getParseGraphBidirectional(
                            workflow.getJsonGraph());    
            executeNextInstancesFromNode(
                    workflow,
                    graph,
                    instance.getWorkflowNode().getId());
        }
    }
    
    private static void startExecution(final Long workflowId) {
        WorkflowModel workflow = 
                WorkflowModel.findById(workflowId);        
        if(workflow == null)
            return;        
        GraphBidirectional<Long> graph = 
                JsonGraph.getParseGraphBidirectional(
                        workflow.getJsonGraph());        
        executeNextInstancesFromNode(
                workflow,
                graph, 
                JsonGraph.START_OPERATOR_ID);        
    }
    
    private static void executeNextInstancesFromNode(
            WorkflowModel workflow,
            final GraphBidirectional<Long> graph,
            final Long fromNodeId) {        
        List<Long> listStartNodeIds = 
                graph.getNextAdjacentNodes(
                        fromNodeId);   
        for (Long nodeId : listStartNodeIds) {
            List<Long> listDependents = 
                    graph.getDependenciesBackwards(nodeId);
            if(!WorkflowNodeModel.hasDependentsFinished(listDependents)) {
                continue;
            }            
            InstanceModel instance = 
                    InstanceModel.findByWorkflowNodeId(nodeId);
            if(instance == null) {
                continue;
            }            
            if(!InstanceCreationJob.executeInstance(
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
