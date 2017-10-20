package app.controllers;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;

import app.common.Response;
import app.common.Response.Type;
import app.common.Routes;
import app.models.FirewallModel;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class NetworkControllerTest {

    @Autowired
    private NetworkController controller;
    @Autowired
    private TestRestTemplate restTemplate;
    @Value("${local.server.port}")
    private int PORT;

    @Test
    public void contexLoads() throws Exception {
        assertThat(controller).isNotNull();
    }

    @Test
    public void creatingNewRuleTest() {

        Response<List<FirewallModel>> responseList = this.restTemplate
                .exchange(
                        Routes.NETWORK_RULES, 
                        HttpMethod.GET, 
                        null,
                        new ParameterizedTypeReference< Response<List<FirewallModel>> >() {})
                .getBody();

        assertThat(responseList.getType()).isEqualTo(Type.SUCCESS);
        assertThat(responseList.getContent()).isNotNull();
        assertThat(responseList.getContent()).isNotNull(); 
        
        List<FirewallModel> currentRules = responseList.getContent();
        Set<Integer> currentPorts = new TreeSet<>();
        for (FirewallModel model : currentRules) {
            currentPorts.add(model.getPort());
        }
        
        Integer portFinder = 5000;
        while(currentPorts.contains(++portFinder));
        
        FirewallModel firewallRule = 
                new FirewallModel(
                        FirewallModel.PROTOCOL.tcp, 
                        portFinder, 
                        new ArrayList<>());        
        

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        HttpEntity<FirewallModel> entity = 
                new HttpEntity<>(firewallRule, headers);
        
        Response<?> responseCreate = this.restTemplate
                .exchange(
                        Routes.NETWORK_RULE, 
                        HttpMethod.POST, 
                        entity,
                        new ParameterizedTypeReference< Response<?> >() {})
                .getBody();        
    }

}
