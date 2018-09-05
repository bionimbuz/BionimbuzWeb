package models;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import play.data.binding.NoBinding;
import play.data.validation.MaxSize;
import play.data.validation.Required;
import play.db.jpa.GenericModel;

@Entity
@Table(name = "tb_workflow")
public class WorkflowModel extends GenericModel {
    
    public static enum WORKFLOW_STATUS {
        EDITING,
        RUNNING,
        STOPPED,
    }

    @Id
    @GeneratedValue
    private Long id;

    @Required
    @MaxSize(100)
    private String name;        
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "workflow")
    @NoBinding
    private List<InstanceModel> listInstances;
    @NoBinding
    private WORKFLOW_STATUS status;
        
    public WorkflowModel() {        
        super();
    }

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
    public List<InstanceModel> getListInstances() {
        return listInstances;
    }
    public void setListInstances(List<InstanceModel> listInstances) {
        this.listInstances = listInstances;
    }
    public WORKFLOW_STATUS getStatus() {
        return status;
    }
    public void setStatus(WORKFLOW_STATUS status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
