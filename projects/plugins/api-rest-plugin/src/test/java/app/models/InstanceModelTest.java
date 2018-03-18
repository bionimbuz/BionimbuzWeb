package app.models;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.junit.Test;

public class InstanceModelTest extends PluginInstanceModel {
    
    @Test
    public void uniqueIdGeneration() throws Exception {  
        
        int generatedIdLen = 10;
        int instancesLen = 10;        
        final String PREFIX = "bnz-inst";
        
        List<PluginInstanceModel> lstInstancesZones = 
                generateRandomListZone(PREFIX, instancesLen);  
        Set<Integer> currentIds = 
                getSetOfCurrentIds(lstInstancesZones, PREFIX); 
        assertThat(currentIds.size()).isEqualTo(instancesLen); 
        
        List<String> namesList = 
                generateUniqueNames(lstInstancesZones, generatedIdLen, PREFIX);        
        
        Set<String> withoutRepeated = new HashSet<>();
        withoutRepeated.addAll(namesList);        
        
        assertThat(withoutRepeated.size()).isEqualTo(generatedIdLen);
        assertThat(isMutualExcluded(currentIds, namesList, PREFIX)).isTrue();
    }
        
    private Integer generateUniqueId(Set<Integer> currentIds, Random rand) {
        Integer generated = -1;
        
        do {generated = rand.nextInt(100) + 1;}
        while(currentIds.contains(generated));
        
        return generated;
    }
    
    private boolean isMutualExcluded(final Set<Integer> currentIds, List<String> namesList, String prefix) {
        
        for (Integer id : currentIds) {
            String name = PluginInstanceModel.generateNameForId(id, prefix);
            if(namesList.contains(name))
                return false; 
        }
        
        for (String name : namesList) {
            Integer id = PluginInstanceModel.extractIdFromName(name, prefix);
            if(currentIds.contains(id))
                return false;            
        }
        
        return true;        
    }
    
    private List<PluginInstanceModel> generateRandomListZone(String instancePrefix, int instSize) {

        Set<Integer> currentIds = new HashSet<>();
        Random rand = new Random();

        List<PluginInstanceModel> lstInstances = new ArrayList<>();
        String instanceType = "type";

        for(int j = 0; j < instSize; j++) {
            Integer id = generateUniqueId(currentIds, rand);
            currentIds.add(id);
            PluginInstanceModel instance = 
                    new PluginInstanceModel(
                            String.valueOf(j), PluginInstanceModel.generateNameForId(id, instancePrefix), 
                            instanceType, 
                            null);
            
            lstInstances.add(instance);
        }
        
        return lstInstances;
    }        
}
