package app.controllers;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

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
import app.models.PluginComputingInstanceModel;
import app.models.PluginComputingZoneModel;
import utils.TestUtils;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class ComputingControllerTest {

    private static final String INSTANCE_ZONE = "us-east1-b";
    private static final String INSTANCE_REGION = "us-east1";
    private static final String INSTANCE_TYPE = "f1-micro";
    private static final String INSTANCE_STARTUP_SCRIPT = "apt-get update && apt-get install -y apache2 && hostname > /var/www/index.html";
    private static final String INSTANCE_IMAGE_URL = "https://www.googleapis.com/compute/v1/projects/ubuntu-os-cloud/global/images/ubuntu-1604-xenial-v20170919";
    
    private static final Integer LENGTH_CREATION = 2;
    
    @Autowired
    private ComputingController controller;
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
        TestUtils.setTimeout(restTemplate.getRestTemplate(), 0);
        ResponseEntity<Body<PluginComputingInstanceModel>> responseGet = null;        
        
        List<PluginComputingInstanceModel> responseList = listAllTest();        
        List<PluginComputingInstanceModel> newInstances = getInstancesToCreate(LENGTH_CREATION); 
        for (PluginComputingInstanceModel model : newInstances) {
            responseGet = getInstanceTest(model);
            assertThat(responseGet.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        }        
        
        List<PluginComputingInstanceModel> createdInstances = createInstancesTest(newInstances);
        assertThat(createdInstances.size()).isEqualTo(LENGTH_CREATION);        

        int initialSize = responseList.size();
        responseList = listAllTest();        
        assertThat(responseList.size()).isEqualTo(initialSize + LENGTH_CREATION);        
           
        for (PluginComputingInstanceModel model : createdInstances) {
            deleteInstanceTest(model);
        }        
        
        for (PluginComputingInstanceModel model : newInstances) {
            responseGet = getInstanceTest(model);
            assertThat(responseGet.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        }   

        responseList = listAllTest();        
        assertThat(responseList.size()).isEqualTo(initialSize);        
    }
    
    @Test
    public void list_Regions_Test() {        
        TestUtils.setTimeout(restTemplate.getRestTemplate(), 0);
        HttpEntity<Void> entity = TestUtils.createEntity(SystemConstants.PLUGIN_COMPUTE_READ_SCOPE);
    
        ResponseEntity< Body<List<PluginComputingZoneModel>> > responseList = 
                this.restTemplate
                    .exchange(
                            Routes.COMPUTING_REGIONS_ZONES.replace("{name}", INSTANCE_REGION),
                            HttpMethod.GET, 
                            entity,
                            new ParameterizedTypeReference< Body<List<PluginComputingZoneModel>> >() {});          
        
        assertThat(responseList.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseList.getBody()).isNotNull();
    }
    
    private void deleteInstanceTest(PluginComputingInstanceModel model) {
        
        HttpEntity<Void> entity = TestUtils.createEntity(SystemConstants.PLUGIN_COMPUTE_WRITE_SCOPE);
        
        ResponseEntity<Body<Boolean>> response = this.restTemplate
                .exchange(
                        Routes.COMPUTING_REGIONS_ZONES_INSTANCES_NAME
                            .replace("{region}", model.getRegion())
                            .replace("{zone}", model.getZone())
                            .replace("{name}", model.getName()), 
                        HttpMethod.DELETE, 
                        entity,
                        new ParameterizedTypeReference< Body<Boolean> >() {}
                        );          
        assertThat(response).isNotNull();    
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
    
    private List<PluginComputingInstanceModel> createInstancesTest(List<PluginComputingInstanceModel> instances){

        
        List<PluginComputingInstanceModel> res = new ArrayList<>();
        
        for (PluginComputingInstanceModel pluginComputingInstanceModel : instances) {
            
            HttpEntity<PluginComputingInstanceModel> entity = 
                    TestUtils.createEntity(pluginComputingInstanceModel, SystemConstants.PLUGIN_COMPUTE_WRITE_SCOPE);
            ResponseEntity<Body<PluginComputingInstanceModel>> response = this.restTemplate
                    .exchange(
                            Routes.COMPUTING_INSTANCES, 
                            HttpMethod.POST, 
                            entity,
                            new ParameterizedTypeReference<Body<PluginComputingInstanceModel>>() {});                     
            assertThat(response).isNotNull();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            
            res.add(response.getBody().getContent());
        }
        
        return res;
    }
    
    private List<PluginComputingInstanceModel> listAllTest() {         

        HttpEntity<Void> entity = TestUtils.createEntity(SystemConstants.PLUGIN_COMPUTE_READ_SCOPE);
        
        ResponseEntity< Body<List<PluginComputingInstanceModel>> > responseList = 
                this.restTemplate
                    .exchange(
                            Routes.COMPUTING_INSTANCES, 
                            HttpMethod.GET, 
                            entity,
                            new ParameterizedTypeReference< Body<List<PluginComputingInstanceModel>> >() {});          
        
        assertThat(responseList.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseList.getBody()).isNotNull();
        return responseList.getBody().getContent();
    }        
    
    private ResponseEntity<Body<PluginComputingInstanceModel>> getInstanceTest(PluginComputingInstanceModel model) {
        
        HttpEntity<Void> entity = TestUtils.createEntity(SystemConstants.PLUGIN_COMPUTE_READ_SCOPE);
        
        ResponseEntity<Body<PluginComputingInstanceModel>> response = 
                this.restTemplate
                    .exchange(
                        Routes.COMPUTING_REGIONS_ZONES_INSTANCES_NAME
                            .replace("{region}", model.getRegion())
                            .replace("{zone}", model.getZone())
                            .replace("{name}", model.getName()), 
                            HttpMethod.GET, 
                            entity,
                            new ParameterizedTypeReference< Body<PluginComputingInstanceModel> >() {}
                        );          
        assertThat(response).isNotNull();  
        return response;
    }
    
    private List<PluginComputingInstanceModel> getInstancesToCreate(int length) {
        List<PluginComputingInstanceModel> instances = new ArrayList<>();
        List<Integer> firewallTcpPorts = new ArrayList<>();
        firewallTcpPorts.add(8080);
        firewallTcpPorts.add(80);

        for(int i=0;i<length;i++) {
            PluginComputingInstanceModel instance = new PluginComputingInstanceModel();
            instance.setImageUrl(INSTANCE_IMAGE_URL);
            instance.setStartupScript(INSTANCE_STARTUP_SCRIPT);
            instance.setType(INSTANCE_TYPE);
            instance.setRegion(INSTANCE_REGION);
            instance.setZone(INSTANCE_ZONE);
            instance.setFirewallTcpPorts(firewallTcpPorts);
            
            instances.add(instance);
        }
        
        return instances;
    }

}
