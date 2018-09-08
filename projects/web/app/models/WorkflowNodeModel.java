package models;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import play.data.binding.NoBinding;
import play.db.jpa.GenericModel;

@Entity
@Table(name = "tb_workflow_node")
public class WorkflowNodeModel extends GenericModel {

    @Id
    @GeneratedValue
    private Long id;
    @OneToOne(cascade = CascadeType.ALL)
    private InstanceModel instance;    
    @NoBinding
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private WorkflowModel workflow;
        
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
