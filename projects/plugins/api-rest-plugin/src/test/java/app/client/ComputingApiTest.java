package app.client;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;

import app.controllers.mocks.ComputingControllerMock;
import app.models.Body;
import app.models.PluginComputingInstanceModel;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class ComputingApiTest {
    
    @Autowired
    private ComputingControllerMock controller;
    @Value("${local.server.port}")
    private int PORT = 0;
    
    @Test
    public void contexLoads() throws Exception {
        assertThat(controller).isNotNull();
    }
        
    @Test
    public void createTest() throws Exception {
        ComputingApi instanceApi = new ComputingApi(getUrl());
        PluginComputingInstanceModel model = createModel();
        
        Body<PluginComputingInstanceModel> body = 
                instanceApi.createInstance(
                        "fake-token", "fake-identity", model);
        PluginComputingInstanceModel modelCreated = body.getContent();
        
        assertThat(model.getName()).isEqualTo(modelCreated.getName());
    }
    
    @Test
    public void getTest() throws Exception {
        ComputingApi instanceApi = new ComputingApi(getUrl());
        PluginComputingInstanceModel model = createModel();  
        String nameCreated = model.getName();
        String zoneCreated = model.getZone();
        String regionCreated = model.getRegion();
        
        Body<PluginComputingInstanceModel> body = 
                instanceApi.getInstance(
                        "fake-token", "fake-identity", regionCreated, zoneCreated, nameCreated);
        assertThat(body.getContent().getName()).isEqualTo(nameCreated);
        assertThat(body.getContent().getZone()).isEqualTo(zoneCreated);
        assertThat(body.getContent().getRegion()).isEqualTo(regionCreated);
    }
    
    @Test
    public void deleteTest() throws Exception {
        ComputingApi instanceApi = new ComputingApi(getUrl());
        PluginComputingInstanceModel model = createModel();  
        String nameCreated = model.getName();
        String zoneCreated = model.getZone();
        String regionCreated = model.getZone();
        
        Body<Boolean> body = 
                instanceApi.deleteInstance(
                        "fake-token", "fake-identity", regionCreated, zoneCreated, nameCreated);
        assertThat(body.getMessage()).isEqualTo(Body.OK);
    }      

    @Test
    public void listTest() throws Exception {
        ComputingApi instanceApi = new ComputingApi(getUrl());
        Body<List<PluginComputingInstanceModel>> body = 
                instanceApi.listInstances(
                        "fake-token", "fake-identity");
        assertThat(body.getMessage()).isEqualTo(Body.OK);
        assertThat(body.getContent()).isNotNull();
        assertThat(body.getContent()).isNotEmpty();
    }
    
    private String getUrl() {
        return "http://localhost:"+PORT;
    }
    
    private static PluginComputingInstanceModel createModel() {
        PluginComputingInstanceModel model = new PluginComputingInstanceModel();
        model.setName("instance-name");
        model.setZone("instance-zone");
        model.setRegion("instance-region");
        return model;
    }
}
