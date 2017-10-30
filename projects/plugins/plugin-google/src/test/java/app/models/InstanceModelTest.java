package app.models;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class InstanceModelTest extends InstanceModel {
    
    @Test
    public void uniqueIdGeneration() throws Exception {  
        
        int generatedIdLen = 10;
        int zonesLen = 3;
        int instancesLen = 10;        
        final String PREFIX = "bnz-inst";
        
        List<InstanceModel> lstZones = 
                generateRandomListZone(PREFIX, instancesLen);  
        Set<Integer> currentIds = 
                getSetOfCurrentIds(lstZones, PREFIX); 
        assertThat(currentIds.size()).isEqualTo(zonesLen * instancesLen); 
        
        List<String> namesList = 
                generateUniqueNames(lstZones, generatedIdLen, PREFIX);        
        
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
            String name = InstanceModel.generateNameForId(id, prefix);
            if(namesList.contains(name))
                return false; 
        }
        
        for (String name : namesList) {
            Integer id = InstanceModel.extractIdFromName(name, prefix);
            if(currentIds.contains(id))
                return false;            
        }
        
        return true;        
    }
    
    private List<InstanceModel> generateRandomListZone(String instancePrefix, int instSize) {

        Set<Integer> currentIds = new HashSet<>();
        Random rand = new Random();

        List<InstanceModel> lstInstances = new ArrayList<>();
        String instanceType = "type";

        for(int j = 0; j < instSize; j++) {
            Integer id = generateUniqueId(currentIds, rand);
            currentIds.add(id);
            InstanceModel instance = 
                    new InstanceModel(
                            String.valueOf(j), InstanceModel.generateNameForId(id, instancePrefix), 
                            instanceType, 
                            null);
            
            lstInstances.add(instance);
        }
        
        return lstInstances;
    }        
}
