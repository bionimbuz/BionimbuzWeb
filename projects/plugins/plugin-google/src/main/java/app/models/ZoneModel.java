package app.models;

import java.util.List;

public class ZoneModel {
    
    private String zone;
    private List<InstanceModel> lstInstances;    

    public ZoneModel(final String zone, final List<InstanceModel> lstInstances) {
        this.zone = zone;
        this.lstInstances = lstInstances;
    }
    
    public List<InstanceModel> getLstInstances() {
        return lstInstances;
    }
    public void setLstInstances(final List<InstanceModel> lstInstances) {
        this.lstInstances = lstInstances;
    }
    public String getZone() {
        return zone;
    }
    public void setZone(final String zone) {
        this.zone = zone;
    }
}
