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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

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

    	ResponseEntity<FirewallModel> responseGet = null;        
        List<FirewallModel> responseList = listAllTest();
        
        FirewallModel firewall = searchAvailableFirewallPort(responseList);     
                        
        responseGet = getRuleTest(firewall);        
        assertThat(responseGet.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        createRuleTest(firewall); 
               
        responseGet = getRuleTest(firewall);        
        assertThat(responseGet.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(firewall.getName())
        		.isEqualTo(responseGet.getBody().getName());   

        deleteRuleTest(firewall);             
        
        responseGet = getRuleTest(firewall);        
        assertThat(responseGet.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

	private void deleteRuleTest(FirewallModel firewall) {
		ResponseEntity<Object> response = this.restTemplate
                .exchange(
                        Routes.NETWORK_RULE+"/"+firewall.getName(), 
                        HttpMethod.DELETE, 
                        null,
                        new ParameterizedTypeReference< Object >() {}
                        );          
        assertThat(response).isNotNull();    
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
	}
    
	private void createRuleTest(FirewallModel firewall) {
		HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);        
        
        HttpEntity<FirewallModel> entity = 
        		new HttpEntity<>(firewall, headers);
        
        ResponseEntity<Object> response = this.restTemplate
                .exchange(
                        Routes.NETWORK_RULE, 
                        HttpMethod.POST, 
                        entity,
                        new ParameterizedTypeReference< Object >() {});                     
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
	}

	private ResponseEntity<FirewallModel> getRuleTest(FirewallModel firewall) {
		ResponseEntity<FirewallModel> response = 
				this.restTemplate
	                .exchange(
	                        Routes.NETWORK_RULE+"/"+firewall.getName(), 
	                        HttpMethod.GET, 
	                        null,
	                        FirewallModel.class
                        );          
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

	private List<FirewallModel> listAllTest() {	
		
        ResponseEntity<List<FirewallModel>> responseList = 
				this.restTemplate
	                .exchange(
	                        Routes.NETWORK_RULES, 
	                        HttpMethod.GET, 
	                        null,
	                        new ParameterizedTypeReference< List<FirewallModel> >() {});          
        
        assertThat(responseList.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseList.getBody()).isNotNull();
		return responseList.getBody();
	}    
}
