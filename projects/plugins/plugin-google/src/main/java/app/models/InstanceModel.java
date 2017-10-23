package app.models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class InstanceModel {

    private String id;
    private String name;
    private String machineType;
    private Date creationDate;  
    private String internalIp;
    private String externalIp;
    
    public InstanceModel() {        
    }
    
    public InstanceModel(
            String id, 
            String name, 
            String machineType,
            Date creationDate) {
        this.id = id;
        this.name = name;
        this.machineType = machineType;
        this.creationDate = creationDate;
    }
    
    public static List<String> generateUniqueNames(
            List<ZoneModel> currentZones, int size, final String prefix) { 
        
        List<String> res = new ArrayList<>();        
        Set<Integer> setOfNameIds = getSetOfCurrentIds(currentZones, prefix);
        
        int newId = setOfNameIds.size();        
        for(int i=0; i<size; i++) {            
            while(setOfNameIds.contains(++newId));
            res.add(generateNameForId(newId, prefix));
        }     
        return res;
    }  
    
    protected static String generateNameForId(final Integer id, final String prefix) {               
        return prefix + "-" + id;     
    }
    
    protected static Integer extractIdFromName(final String name, final String prefix) {
        String [] nameSpplited = name.split(prefix);        
        if(nameSpplited.length != 2)
            return -1;        
        return Integer.parseInt(nameSpplited[1].replaceAll("-", ""));     
    }
    
    protected static Set<Integer> getSetOfCurrentIds(final List<ZoneModel> currentZones, final String prefix) {        
        Set<Integer> ids = new TreeSet<>();   
        for (ZoneModel zoneModel : currentZones) {
            for (InstanceModel instanceModel : zoneModel.getLstInstances()) {                
                Integer id = extractIdFromName(instanceModel.getName(), prefix);  
                if(id >= 0) {
                    ids.add(id);
                }
            }
        }
        return ids;
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
