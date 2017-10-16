package app.models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class InstanceModel {

    private String id;
    private String name;
    private String machineType;
    private Date creationDate;  
    private String internalIp;
    private String externalIp;
    
    public InstanceModel(
            String id, 
            String name, 
            String machineType,
            Date creationDate) {
        super();
        this.id = id;
        this.name = name;
        this.machineType = machineType;
        this.creationDate = creationDate;
    }
    
    public static List<String> generateUniqueNames(
            List<ZoneModel> zones, int size) {
        
        List<String> res = new ArrayList<>();
        return res;
    }  
    
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getMachineType() {
        return machineType;
    }
    public void setMachineType(String machineType) {
        this.machineType = machineType;
    }
    public Date getCreationDate() {
        return creationDate;
    }
    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }
    public String getInternalIp() {
        return internalIp;
    }
    public void setInternalIp(String internalIp) {
        this.internalIp = internalIp;
    }
    public String getExternalIp() {
        return externalIp;
    }
    public void setExternalIp(String externalIp) {
        this.externalIp = externalIp;
    }
}
