package models;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import play.data.validation.Required;
import play.db.jpa.GenericModel;

@Entity
@Table(name = "tb_workflow_node")
public class WorkflowNodeModel extends GenericModel {

    @Id
    @GeneratedValue
    private Long id;
    @Required
    @ManyToOne(fetch = FetchType.LAZY)
    private WorkflowModel workflow;
    @OneToOne
    private InstanceModel instance;
    
        
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public WorkflowModel getWorkflow() {
        return workflow;
    }
    public void setWorkflow(WorkflowModel workflow) {
        this.workflow = workflow;
    }
    public InstanceModel getInstance() {
        return instance;
    }
    public void setInstance(InstanceModel instance) {
        this.instance = instance;
    }
}
