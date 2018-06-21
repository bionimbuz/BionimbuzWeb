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
import app.common.SystemConstants;
import app.models.Body;
import app.models.PluginFirewallModel;
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

    	ResponseEntity<Body<PluginFirewallModel>> responseGet = null;        
        List<PluginFirewallModel> responseList = listAllTest();
        
        PluginFirewallModel firewall = searchAvailableFirewallPort(responseList);     
                        
        responseGet = getRuleTest(firewall);        
        assertThat(responseGet.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        createRuleTest(firewall, this.restTemplate); 
               
        responseGet = getRuleTest(firewall);        
        assertThat(responseGet.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(firewall.getName())
        		.isEqualTo(responseGet.getBody().getContent().getName());   

        deleteRuleTest(firewall, this.restTemplate);             
        
        responseGet = getRuleTest(firewall);        
        assertThat(responseGet.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

	public static void deleteRuleTest(PluginFirewallModel model, final TestRestTemplate restTemplate) {
	    
        HttpEntity<Void> entity = TestUtils.createEntity(SystemConstants.PLUGIN_COMPUTE_WRITE_SCOPE);
        
		ResponseEntity<Body<Boolean>> response = 
		        restTemplate
                    .exchange(
                            Routes.FIREWALLS+"/"+model.getName(), 
                            HttpMethod.DELETE, 
                            entity,
                            new ParameterizedTypeReference< Body<Boolean> >() {});          
        assertThat(response).isNotNull();    
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
	}
    
	public static void createRuleTest(PluginFirewallModel firewall, final TestRestTemplate restTemplate) {

        HttpEntity<PluginFirewallModel> entity = TestUtils.createEntity(firewall, SystemConstants.PLUGIN_COMPUTE_WRITE_SCOPE);
        
        ResponseEntity<Body<PluginFirewallModel>> response = 
                restTemplate
                    .exchange(
                            Routes.FIREWALLS, 
                            HttpMethod.POST, 
                            entity,
                            new ParameterizedTypeReference< Body<PluginFirewallModel> >() {});                     
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
	}

	private ResponseEntity<Body<PluginFirewallModel>> getRuleTest(PluginFirewallModel firewall) {
	    
        HttpEntity<Void> entity = TestUtils.createEntity(SystemConstants.PLUGIN_COMPUTE_READ_SCOPE);
        
		ResponseEntity<Body<PluginFirewallModel>> response = 
				this.restTemplate
	                .exchange(
	                        Routes.FIREWALLS+"/"+firewall.getName(), 
	                        HttpMethod.GET, 
	                        entity,
                            new ParameterizedTypeReference<Body<PluginFirewallModel>>() {});          
        assertThat(response).isNotNull();  
        return response;
	}

	private List<PluginFirewallModel> listAllTest() {			

        HttpEntity<Void> entity = TestUtils.createEntity(SystemConstants.PLUGIN_COMPUTE_READ_SCOPE);
	    
        ResponseEntity< Body<List<PluginFirewallModel>> > responseList = 
				this.restTemplate
	                .exchange(
	                        Routes.FIREWALLS, 
	                        HttpMethod.GET, 
	                        entity,
	                        new ParameterizedTypeReference< Body<List<PluginFirewallModel>> >() {});      
        assertThat(responseList.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseList.getBody()).isNotNull();
		return responseList.getBody().getContent();
	}        

    private PluginFirewallModel searchAvailableFirewallPort(List<PluginFirewallModel> currentRules) {
        Set<Integer> currentPorts = new TreeSet<>();
        for (PluginFirewallModel model : currentRules) {
            currentPorts.add(model.getPort());
        }
        
        Integer portFinder = 5000;
        while(currentPorts.contains(++portFinder));
        
        PluginFirewallModel firewall = 
                new PluginFirewallModel(
                        PluginFirewallModel.PROTOCOL.tcp, 
                        portFinder, 
                        new ArrayList<>());
        return firewall;
    }

}
