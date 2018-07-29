package app.controllers;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
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
import app.models.PluginComputingRegionModel;
import utils.TestUtils;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class ComputingControllerTest {

    private static final String INSTANCE_ZONE = "us-east1-b";
    private static final String INSTANCE_REGION = "us-east1";
    private static final String INSTANCE_TYPE = "f1-micro";
    private static final String INSTANCE_STARTUP_SCRIPT = "apt-get update && apt-get install -y apache2 && hostname > /var/www/index.html";
    private static final String INSTANCE_IMAGE_URL = "https://www.googleapis.com/compute/v1/projects/ubuntu-os-cloud/global/images/ubuntu-1604-xenial-v20170919";
    
    private static final Integer STARTUP_SCRIPT_APACHE_PORT = 80;
    private static final Integer WAIT_MS_TO_START_INSTANCE = 60 * 1000;
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
    
        ResponseEntity< Body<List<PluginComputingRegionModel>> > responseList = 
                this.restTemplate
                    .exchange(
                            Routes.COMPUTING_REGIONS,
                            HttpMethod.GET, 
                            entity,
                            new ParameterizedTypeReference< Body<List<PluginComputingRegionModel>> >() {});          
        
        assertThat(responseList.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseList.getBody()).isNotNull();
    }
    
    private void doHttGetInInstancesTest(PluginComputingInstanceModel model) {
        try {            
            String url = "http://" + model.getExternalIp() + ":" + STARTUP_SCRIPT_APACHE_PORT;
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("GET");

            int responseCode = con.getResponseCode();
            assertThat(responseCode).isEqualTo(200);
            
            try(BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()))) {
                
                String inputLine;
                StringBuffer response = new StringBuffer();
        
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                
                assertThat(response).isNotBlank();
            }        
        } catch (Exception e) {
            e.printStackTrace();
            assertThat(e).isNull();
        }
        
    }
    
    private void deleteInstanceTest(PluginComputingInstanceModel model) {
        
        HttpEntity<Void> entity = TestUtils.createEntity(SystemConstants.PLUGIN_COMPUTE_WRITE_SCOPE);
        
        ResponseEntity<Body<Boolean>> response = this.restTemplate
                .exchange(
                        Routes.COMPUTING_INSTANCES+"/"+model.getZone() + "/"+model.getName(), 
                        HttpMethod.DELETE, 
                        entity,
                        new ParameterizedTypeReference< Body<Boolean> >() {}
                        );          
        assertThat(response).isNotNull();    
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
    
    private List<PluginComputingInstanceModel> createInstancesTest(List<PluginComputingInstanceModel> instances){
        HttpEntity<List<PluginComputingInstanceModel>> entity = 
                TestUtils.createEntity(instances, SystemConstants.PLUGIN_COMPUTE_WRITE_SCOPE);
        
        ResponseEntity<Body<List<PluginComputingInstanceModel>>> response = this.restTemplate
                .exchange(
                        Routes.COMPUTING_INSTANCES, 
                        HttpMethod.POST, 
                        entity,
                        new ParameterizedTypeReference<Body<List<PluginComputingInstanceModel>>>() {});                     
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        
        return response.getBody().getContent();
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
                            Routes.COMPUTING_INSTANCES+"/"+model.getZone() + "/"+model.getName(), 
                            HttpMethod.GET, 
                            entity,
                            new ParameterizedTypeReference< Body<PluginComputingInstanceModel> >() {}
                        );          
        assertThat(response).isNotNull();  
        return response;
    }
    
    private List<PluginComputingInstanceModel> getInstancesToCreate(int length) {
        List<PluginComputingInstanceModel> instances = new ArrayList<>();

        for(int i=0;i<length;i++) {
            PluginComputingInstanceModel instance = new PluginComputingInstanceModel();
            instance.setImageUrl(INSTANCE_IMAGE_URL);
            instance.setStartupScript(INSTANCE_STARTUP_SCRIPT);
            instance.setType(INSTANCE_TYPE);
            instance.setRegion(INSTANCE_REGION);
            instance.setZone(INSTANCE_ZONE);
            
            instances.add(instance);
        }
        
        return instances;
    }

}
