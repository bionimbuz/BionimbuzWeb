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
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import app.common.Routes;
import app.common.TestUtils;
import app.models.CredentialModel;
import app.models.FirewallModel;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class FirewallControllerTest {

    @Autowired
    private FirewallController controller;
    @Autowired
    private TestRestTemplate restTemplate;
    @Value("${local.server.port}")
    private int PORT;

    @Test
    public void contexLoads() throws Exception {
        assertThat(controller).isNotNull();
    }

    @Test
    public void CRUD_Test() {

    	ResponseEntity<FirewallModel> responseGet = null;        
        List<FirewallModel> responseList = listAllTest();
        
        FirewallModel firewall = searchAvailableFirewallPort(responseList);     
                        
        responseGet = getRuleTest(firewall);        
        assertThat(responseGet.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        createRuleTest(firewall, this.restTemplate); 
               
        responseGet = getRuleTest(firewall);        
        assertThat(responseGet.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(firewall.getName())
        		.isEqualTo(responseGet.getBody().getName());   

        deleteRuleTest(firewall, this.restTemplate);             
        
        responseGet = getRuleTest(firewall);        
        assertThat(responseGet.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

	public static void deleteRuleTest(FirewallModel model, TestRestTemplate restTemplate) {
	    
        HttpEntity<CredentialModel<Void>> entity = TestUtils.createEntity();
        
		ResponseEntity<Object> response = restTemplate
                .exchange(
                        Routes.FIREWALL+"/"+model.getName(), 
                        HttpMethod.DELETE, 
                        entity,
                        new ParameterizedTypeReference< Object >() {}
                        );          
        assertThat(response).isNotNull();    
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
	}
    
	public static void createRuleTest(FirewallModel firewall, TestRestTemplate restTemplate) {

        HttpEntity<CredentialModel<FirewallModel>> entity = TestUtils.createEntity(firewall);
        
        ResponseEntity<Object> response = restTemplate
                .exchange(
                        Routes.FIREWALL, 
                        HttpMethod.POST, 
                        entity,
                        new ParameterizedTypeReference< Object >() {});                     
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
	}

	private ResponseEntity<FirewallModel> getRuleTest(FirewallModel firewall) {
	    
        HttpEntity<CredentialModel<Void>> entity = TestUtils.createEntity();
        
		ResponseEntity<FirewallModel> response = 
				this.restTemplate
	                .exchange(
	                        Routes.FIREWALL+"/"+firewall.getName(), 
	                        HttpMethod.POST, 
	                        entity,
	                        FirewallModel.class
                        );          
        assertThat(response).isNotNull();  
        return response;
	}

	private List<FirewallModel> listAllTest() {			

        HttpEntity<CredentialModel<Void>> entity = TestUtils.createEntity();
	    
        ResponseEntity<List<FirewallModel>> responseList = 
				this.restTemplate
	                .exchange(
	                        Routes.FIREWALLS, 
	                        HttpMethod.POST, 
	                        entity,
	                        new ParameterizedTypeReference< List<FirewallModel> >() {});          
        
        assertThat(responseList.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseList.getBody()).isNotNull();
		return responseList.getBody();
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

}
