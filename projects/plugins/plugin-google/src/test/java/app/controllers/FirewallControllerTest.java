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
import app.models.FirewallModel;
import utils.TestUtils;

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

	public static void deleteRuleTest(FirewallModel model, final TestRestTemplate restTemplate) {
	    
        HttpEntity<Void> entity = TestUtils.createEntity(TestUtils.WRITE_SCOPE);
        
		ResponseEntity<Object> response = 
		        restTemplate
                    .exchange(
                            Routes.FIREWALLS+"/"+model.getName(), 
                            HttpMethod.DELETE, 
                            entity,
                            new ParameterizedTypeReference< Object >() {});          
        assertThat(response).isNotNull();    
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
	}
    
	public static void createRuleTest(FirewallModel firewall, final TestRestTemplate restTemplate) {

        HttpEntity<FirewallModel> entity = TestUtils.createEntity(firewall, TestUtils.WRITE_SCOPE);
        
        ResponseEntity<Object> response = 
                restTemplate
                    .exchange(
                            Routes.FIREWALLS, 
                            HttpMethod.POST, 
                            entity,
                            new ParameterizedTypeReference< Object >() {});                     
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
	}

	private ResponseEntity<FirewallModel> getRuleTest(FirewallModel firewall) {
	    
        HttpEntity<Void> entity = TestUtils.createEntity(TestUtils.READ_SCOPE);
        
		ResponseEntity<FirewallModel> response = 
				this.restTemplate
	                .exchange(
	                        Routes.FIREWALLS+"/"+firewall.getName(), 
	                        HttpMethod.GET, 
	                        entity,
	                        FirewallModel.class);          
        assertThat(response).isNotNull();  
        return response;
	}

	private List<FirewallModel> listAllTest() {			

        HttpEntity<Void> entity = TestUtils.createEntity(TestUtils.READ_SCOPE);
	    
        ResponseEntity<List<FirewallModel>> responseList = 
				this.restTemplate
	                .exchange(
	                        Routes.FIREWALLS, 
	                        HttpMethod.GET, 
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
