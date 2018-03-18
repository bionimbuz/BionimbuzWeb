package app.client;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;

import app.controllers.mocks.InstanceControllerMock;
import app.models.Body;
import app.models.PluginInstanceModel;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class InstanceApiTest {
    
    @Autowired
    private InstanceControllerMock controller;
    @Value("${local.server.port}")
    private int PORT = 0;
    
    @Test
    public void contexLoads() throws Exception {
        assertThat(controller).isNotNull();
    }
        
    @Test
    public void createTest() throws Exception {
        InstanceApi instanceApi = new InstanceApi(getUrl());
        List<PluginInstanceModel> listModel = createListModel();  
        
        Body<List<PluginInstanceModel>> body = 
                instanceApi.createInstance(
                        "fake-token", "fake-identity", listModel);
        List<PluginInstanceModel> listModelCreated = body.getContent();
        
        Iterator<PluginInstanceModel> 
            iteratorNew = listModel.iterator(),
            iteratorCreated = listModelCreated.iterator();
        
        while(iteratorNew.hasNext() && iteratorCreated.hasNext()) {
            assertThat(iteratorNew.next().getName())
                .isEqualTo(iteratorCreated.next().getName());
        }
    }
    
    @Test
    public void getTest() throws Exception {
        InstanceApi instanceApi = new InstanceApi(getUrl());
        PluginInstanceModel model = createModel();  
        String nameCreated = model.getName();
        String zoneCreated = model.getZone();
        
        Body<PluginInstanceModel> body = 
                instanceApi.getInstance(
                        "fake-token", "fake-identity", zoneCreated, nameCreated);
        assertThat(body.getContent().getName()).isEqualTo(nameCreated);
        assertThat(body.getContent().getZone()).isEqualTo(zoneCreated);
    }
    
    @Test
    public void deleteTest() throws Exception {
        InstanceApi instanceApi = new InstanceApi(getUrl());
        PluginInstanceModel model = createModel();  
        String nameCreated = model.getName();
        String zoneCreated = model.getZone();
        
        Body<Void> body = 
                instanceApi.deleteInstance(
                        "fake-token", "fake-identity", zoneCreated, nameCreated);
        assertThat(body.getMessage()).isEqualTo(Body.OK);
    }      

    @Test
    public void listTest() throws Exception {
        InstanceApi instanceApi = new InstanceApi(getUrl());
        Body<List<PluginInstanceModel>> body = 
                instanceApi.listInstances(
                        "fake-token", "fake-identity");
        assertThat(body.getMessage()).isEqualTo(Body.OK);
        assertThat(body.getContent()).isNotNull();
        assertThat(body.getContent()).isNotEmpty();
    }
    
    private String getUrl() {
        return "http://localhost:"+PORT;
    }
    
    private static List<PluginInstanceModel> createListModel(){
        List<PluginInstanceModel> listModel = new ArrayList<>();
        listModel.add(createModel());
        listModel.add(createModel());
        return listModel;
    }
    
    private static PluginInstanceModel createModel() {
        PluginInstanceModel model = new PluginInstanceModel();
        model.setName("instance-name");
        model.setZone("instance-zone");
        return model;
    }
}
