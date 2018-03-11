package models;

import java.util.List;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

@Entity
@DiscriminatorValue("E")
public class ExecutorModel extends ApplicationModel {

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "executor")
    private List<InstanceModel> listInstances;

    public List<InstanceModel> getListInstances() {
        return listInstances;
    }
    public void setListInstances(List<InstanceModel> listInstances) {
        this.listInstances = listInstances;
    }
}
