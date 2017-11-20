package models;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import play.db.jpa.GenericModel;

@Entity
@Table(name = "tb_test_relation")
public class TestRelationModel extends GenericModel {

    @Id
    @GeneratedValue
    private Long id;
    private String name; 
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "relationField")
    private List<TestModel> listFields;
    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "multiSelectField")
    private List<TestModel> listMultiFields;    
    
    public TestRelationModel(String name) {
        super();
        this.name = name;
    }

    public TestRelationModel() {
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
    public List<TestModel> getListFields() {
        return listFields;
    }
    public void setListFields(List<TestModel> listFields) {
        this.listFields = listFields;
    }
    public List<TestModel> getListMultiFields() {
        return listMultiFields;
    }
    public void setListMultiFields(List<TestModel> listMultiFields) {
        this.listMultiFields = listMultiFields;
    }
}
