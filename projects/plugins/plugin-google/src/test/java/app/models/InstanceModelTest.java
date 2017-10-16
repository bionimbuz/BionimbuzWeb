package app.models;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class InstanceModelTest {
    
    @Test
    public void helloWorldTest() throws Exception {                
        List<String> namesList = 
                InstanceModel.generateUniqueNames(null, 0);
        assertThat(namesList).isNotNull();
    }
}
