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

import app.controllers.mocks.FirewallControllerMock;
import app.models.Body;
import app.models.FirewallModel;
import app.models.FirewallModel.PROTOCOL;

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
        FirewallApi firewallApi = new FirewallApi(getUrl());
        FirewallModel model = createModel();  
        String nameCreated = model.getName();
        assertThat(nameCreated).isNotEmpty();
        
        Body<FirewallModel> body = 
                firewallApi.replaceRule(
                        "fake-token", "fake-identity", model);
        assertThat(body.getContent().getName()).isEqualTo(nameCreated);
    }
    
    @Test
    public void getTest() throws Exception {
        FirewallApi firewallApi = new FirewallApi(getUrl());
        FirewallModel model = createModel();  
        String nameCreated = model.getName();
        assertThat(nameCreated).isNotEmpty();
        
        Body<FirewallModel> body = 
                firewallApi.getRule(
                        "fake-token", "fake-identity", nameCreated);
        assertThat(body.getContent().getName()).isEqualTo(nameCreated);
    }
    
    @Test
    public void deleteTest() throws Exception {
        FirewallApi firewallApi = new FirewallApi(getUrl());
        FirewallModel model = createModel();  
        String nameCreated = model.getName();
        assertThat(nameCreated).isNotEmpty();
        
        Body<Void> body = 
                firewallApi.deleteRule(
                        "fake-token", "fake-identity", nameCreated);
        assertThat(body.getMessage()).isEqualTo(Body.OK);
    }    

    @Test
    public void listTest() throws Exception {
        FirewallApi firewallApi = new FirewallApi(getUrl());
        Body<List<FirewallModel>> body = 
                firewallApi.listRules(
                        "fake-token", "fake-identity");
        assertThat(body.getMessage()).isEqualTo(Body.OK);
        assertThat(body.getContent()).isNotNull();
        assertThat(body.getContent()).isNotEmpty();
    }
    
    private String getUrl() {
        return "http://localhost:"+PORT;
    }
    
    private static FirewallModel createModel() {
        return new FirewallModel(
                PROTOCOL.tcp,
                8080,
                new ArrayList<String>());   
    }
}
