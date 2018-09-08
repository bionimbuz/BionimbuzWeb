package models;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

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
    }

    @Id
    @GeneratedValue
    private Long id;
    @MaxSize(100)
    private String name;        
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "workflow")
    @NoBinding
    private List<WorkflowNodeModel> listWorkflowNodes;
    @NoBinding
    private WORKFLOW_STATUS status;
    @NoBinding
    private Date creationDate;
    @Column(length=3000)
    private String jsonModel;
    @Column(length=3000)
    private String jsonGraph;
        
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

    @Override
    public String toString() {
        return this.name;
    }
}
