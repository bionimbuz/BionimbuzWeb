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
    public void rule_CRUD_Test() {

        Response<FirewallModel> responseGet = null;        
        Response<List<FirewallModel>> responseList = listAllTest();
        
        FirewallModel firewall = searchAvailableFirewallPort(responseList.getContent());     
                        
        responseGet = getRuleTest(firewall);        
        assertThat(responseGet.getType()).isEqualTo(Type.ERROR);

        createRuleTest(firewall); 
               
        responseGet = getRuleTest(firewall);        
        assertThat(responseGet.getType()).isEqualTo(Type.SUCCESS);
        assertThat(firewall.getName())
        		.isEqualTo(responseGet.getContent().getName());   

        deleteRuleTest(firewall);             
        
        responseGet = getRuleTest(firewall);        
        assertThat(responseGet.getType()).isEqualTo(Type.ERROR);
    }

	private void deleteRuleTest(FirewallModel firewall) {
		Response<Object> response = this.restTemplate
                .exchange(
                        Routes.NETWORK_RULE+"/"+firewall.getName(), 
                        HttpMethod.DELETE, 
                        null,
                        new ParameterizedTypeReference< Response<Object> >() {}
                        )
                .getBody();          
        assertThat(response).isNotNull();    
        assertThat(response.getType()).isEqualTo(Type.SUCCESS);
	}
    
	private void createRuleTest(FirewallModel firewall) {
		HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);        
        
        HttpEntity<FirewallModel> entity = 
        		new HttpEntity<>(firewall, headers);
        
        Response<?> response = this.restTemplate
                .exchange(
                        Routes.NETWORK_RULE, 
                        HttpMethod.POST, 
                        entity,
                        new ParameterizedTypeReference< Response<?> >() {})
                .getBody();                     
        assertThat(response).isNotNull();
        assertThat(response.getType()).isEqualTo(Type.SUCCESS);
	}

	private Response<FirewallModel> getRuleTest(FirewallModel firewall) {
		Response<FirewallModel> response = this.restTemplate
                .exchange(
                        Routes.NETWORK_RULE+"/"+firewall.getName(), 
                        HttpMethod.GET, 
                        null,
                        new ParameterizedTypeReference< Response<FirewallModel> >() {}
                        )
                .getBody();          
        assertThat(response).isNotNull();  
        return response;
	}

	private FirewallModel searchAvailableFirewallPort(List<FirewallModel> currentRules) {
        Set<Integer> currentPorts = new TreeSet<>();
        for (FirewallModel model : currentRules) {
            currentPorts.add(model.getPort());
        }
        
        Integer portFinder = 5000;
        while(currentPorts.contains(++portFinder));
        
        FirewallModel firewall = 
                new FirewallModel(
                        FirewallModel.PROTOCOL.tcp, 
                        portFinder, 
                        new ArrayList<>());
		return firewall;
	}

	private Response<List<FirewallModel>> listAllTest() {
		Response<List<FirewallModel>> responseList = this.restTemplate
                .exchange(
                        Routes.NETWORK_RULES, 
                        HttpMethod.GET, 
                        null,
                        new ParameterizedTypeReference< Response<List<FirewallModel>> >() {})
                .getBody();

        assertThat(responseList.getType()).isEqualTo(Type.SUCCESS);
        assertThat(responseList.getContent()).isNotNull();
		return responseList;
	}    
}
