package app.client;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;

import app.common.GlobalConstants;
import app.controllers.mocks.FirewallControllerMock;
import app.models.Body;
import app.models.FirewallModel;
import app.models.FirewallModel.PROTOCOL;
import retrofit2.Call;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class FirewallApiTest {
    
    @Autowired
    private FirewallControllerMock controller;
    @Value("${local.server.port}")
    private int PORT = 0;
    
    @Test
    public void contexLoads() throws Exception {
        assertThat(controller).isNotNull();
    }
        
    @Test
    public void replaceTest() throws Exception {

        FirewallApi firewallApi = createApi();
        FirewallModel model = createModel();  
        String nameCreated = model.getName();
        assertThat(nameCreated).isNotEmpty();
        
        Call<Body<FirewallModel>> call = 
                firewallApi.replaceRule(
                        GlobalConstants.API_VERSION, 
                        "fake-token", "fake-identity", model);
        Body<FirewallModel> body = call.execute().body();
        assertThat(body.getContent().getName()).isEqualTo(nameCreated);
    }
    
    @Test
    public void getTest() throws Exception {

        FirewallApi firewallApi = createApi();
        FirewallModel model = createModel();  
        String nameCreated = model.getName();
        assertThat(nameCreated).isNotEmpty();
        
        Call<Body<FirewallModel>> call = 
                firewallApi.getRule(
                        GlobalConstants.API_VERSION, 
                        "fake-token", "fake-identity", nameCreated);
        Body<FirewallModel> body = call.execute().body();
        assertThat(body.getContent().getName()).isEqualTo(nameCreated);
    }
    
    @Test
    public void deleteTest() throws Exception {

        FirewallApi firewallApi = createApi();
        FirewallModel model = createModel();  
        String nameCreated = model.getName();
        assertThat(nameCreated).isNotEmpty();
        
        Call<Body<Void>> call = 
                firewallApi.deleteRule(
                        GlobalConstants.API_VERSION, 
                        "fake-token", "fake-identity", nameCreated);
        Body<Void> body = call.execute().body();
        assertThat(body.getMessage()).isEqualTo(Body.OK);
    }    

    @Test
    public void listTest() throws Exception {
        FirewallApi firewallApi = createApi();
        Call<Body<List<FirewallModel>>> call = 
                firewallApi.listRules(
                        GlobalConstants.API_VERSION, 
                        "fake-token", "fake-identity");
        Body<List<FirewallModel>> body = call.execute().body();
        assertThat(body.getMessage()).isEqualTo(Body.OK);
        assertThat(body.getContent()).isNotNull();
        assertThat(body.getContent()).isNotEmpty();
    }
    
    private String getUrl() {
        return "http://localhost:"+PORT;
    }
    
    private FirewallApi createApi() {
        PluginApi pluginApi = new PluginApi(getUrl());
        return pluginApi.createApi(FirewallApi.class); 
    }
    
    private static FirewallModel createModel() {
        return new FirewallModel(
                PROTOCOL.tcp,
                8080,
                new ArrayList<String>());   
    }
}
