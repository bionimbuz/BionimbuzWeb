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

import app.common.GlobalConstants;
import app.controllers.mocks.InstanceControllerMock;
import app.models.Body;
import app.models.InstanceModel;
import retrofit2.Call;

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
        
//    @Test
    public void createInstanceTest() throws Exception {

        InstanceApi instanceApi = createApi();
        List<InstanceModel> listModel = createListModel();  
        
        Call<Body<List<InstanceModel>>> call = 
                instanceApi.createInstance(
                        GlobalConstants.API_VERSION, 
                        "fake-token", "fake-identity", listModel);
        Body<List<InstanceModel>> body = call.execute().body();
        List<InstanceModel> listModelCreated = body.getContent();
        
        Iterator<InstanceModel> 
            iteratorNew = listModel.iterator(),
            iteratorCreated = listModelCreated.iterator();
        
        while(iteratorNew.hasNext() && iteratorCreated.hasNext()) {
            assertThat(iteratorNew.next().getName())
                .isEqualTo(iteratorCreated.next().getName());
        }
    }
    
//    @Test
    public void getInstanceTest() throws Exception {

        InstanceApi instanceApi = createApi();
        InstanceModel model = createModel();  
        String nameCreated = model.getName();
        String zoneCreated = model.getZone();
        
        Call<Body<InstanceModel>> call = 
                instanceApi.getInstance(
                        GlobalConstants.API_VERSION, 
                        "fake-token", "fake-identity", zoneCreated, nameCreated);
        Body<InstanceModel> body = call.execute().body();
        assertThat(body.getContent().getName()).isEqualTo(nameCreated);
        assertThat(body.getContent().getZone()).isEqualTo(zoneCreated);
    }
    
    @Test
    public void deleteInstanceTest() throws Exception {

        InstanceApi instanceApi = createApi();
        InstanceModel model = createModel();  
        String nameCreated = model.getName();
        String zoneCreated = model.getZone();
        
        Call<Body<Void>> call = 
                instanceApi.deleteInstance(
                        GlobalConstants.API_VERSION, 
                        "fake-token", "fake-identity", zoneCreated, nameCreated);
        Body<Void> body = call.execute().body();
        assertThat(body.getMessage()).isEqualTo(Body.OK);
    }      

//    @Test
    public void listRulesTest() throws Exception {
        InstanceApi instanceApi = createApi();
        Call<Body<List<InstanceModel>>> call = 
                instanceApi.listInstances(
                        GlobalConstants.API_VERSION, 
                        "fake-token", "fake-identity");
        Body<List<InstanceModel>> body = call.execute().body();
        assertThat(body.getMessage()).isEqualTo(Body.OK);
        assertThat(body.getContent()).isNotNull();
        assertThat(body.getContent()).isNotEmpty();
    }
    
    private String getUrl() {
        return "http://localhost:"+PORT;
    }
    
    private InstanceApi createApi() {
        PluginApi pluginApi = new PluginApi(getUrl());
        return pluginApi.createApi(InstanceApi.class); 
    }
    
    private static List<InstanceModel> createListModel(){
        List<InstanceModel> listModel = new ArrayList<>();
        listModel.add(createModel());
        listModel.add(createModel());
        return listModel;
    }
    
    private static InstanceModel createModel() {
        InstanceModel model = new InstanceModel();
        model.setName("instance-name");
        model.setZone("instance-zone");
        return model;
    }
}
