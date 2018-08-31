package app.models;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.junit.Test;

public class InstanceModelTest extends PluginComputingInstanceModel {
    
    @Test
    public void uniqueIdGeneration() throws Exception {  
        
        int generatedIdLen = 10;
        int instancesLen = 10;        
        final String PREFIX = "bnz-inst";
        
        List<PluginComputingInstanceModel> lstInstancesZones = 
                generateRandomListZone(PREFIX, instancesLen);  
        Set<Integer> currentIds = 
                getSetOfCurrentIds(lstInstancesZones, PREFIX); 
        assertThat(currentIds.size()).isEqualTo(instancesLen); 
        
        String namesList = 
                generateUniqueName(lstInstancesZones, PREFIX);        
        
        Set<String> withoutRepeated = new HashSet<>();
        withoutRepeated.add(namesList);        
        
        assertThat(withoutRepeated.size()).isEqualTo(generatedIdLen + 1);
    }
        
    private Integer generateUniqueId(Set<Integer> currentIds, Random rand) {
        Integer generated = -1;
        
        do {generated = rand.nextInt(100) + 1;}
        while(currentIds.contains(generated));
        
        return generated;
    }    
    
    private List<PluginComputingInstanceModel> generateRandomListZone(String instancePrefix, int instSize) {

        Set<Integer> currentIds = new HashSet<>();
        Random rand = new Random();

        List<PluginComputingInstanceModel> lstInstances = new ArrayList<>();
        String instanceType = "type";

        for(int j = 0; j < instSize; j++) {
            Integer id = generateUniqueId(currentIds, rand);
            currentIds.add(id);
            PluginComputingInstanceModel instance = 
                    new PluginComputingInstanceModel(
                            String.valueOf(j), PluginComputingInstanceModel.generateNameForId(id, instancePrefix), 
                            instanceType, 
                            null);
            
            lstInstances.add(instance);
        }
        
        return lstInstances;
    }        
}
