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
import app.models.CredentialModel;
import app.models.FirewallModel;
import app.models.InstanceModel;
import utils.TestUtils;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class InstanceControllerTest {

    private static final String INSTANCE_ZONE = "us-east1-b";
    private static final String INSTANCE_REGION = "us-east1";
    private static final String INSTANCE_TYPE = "f1-micro";
    private static final String INSTANCE_STARTUP_SCRIPT = "apt-get update && apt-get install -y apache2 && hostname > /var/www/index.html";
    private static final String INSTANCE_IMAGE_URL = "https://www.googleapis.com/compute/v1/projects/ubuntu-os-cloud/global/images/ubuntu-1604-xenial-v20170919";
    
    private static final Integer STARTUP_SCRIPT_APACHE_PORT = 80;
    private static final Integer WAIT_MS_TO_START_INSTANCE = 20 * 1000;
    private static final Integer LENGTH_CREATION = 2;
    
    @Autowired
    private InstanceController controller;
    @Autowired
    private TestRestTemplate restTemplate;
    @Value("${local.server.port}")
    private int PORT;        
    
    private static FirewallModel getApacheFirewallRule() {
        return new FirewallModel(
                        FirewallModel.PROTOCOL.tcp, 
                        STARTUP_SCRIPT_APACHE_PORT, 
                        new ArrayList<>());
    }
    

    @Test
    public void contexLoads() throws Exception {
        assertThat(controller).isNotNull();
    }

    @Test
    public void CRUD_Test() {
        ResponseEntity<InstanceModel> responseGet = null;        
        
        List<InstanceModel> responseList = listAllTest();        
        List<InstanceModel> newInstances = getInstancesToCreate(LENGTH_CREATION); 
        for (InstanceModel model : newInstances) {
            responseGet = getInstanceTest(model);
            assertThat(responseGet.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        }        
        
        List<InstanceModel> createdInstances = createInstancesTest(newInstances);
        assertThat(createdInstances.size()).isEqualTo(LENGTH_CREATION);        

        int initialSize = responseList.size();
        responseList = listAllTest();        
        assertThat(responseList.size()).isEqualTo(initialSize + LENGTH_CREATION);
        
        try {
            FirewallControllerTest.createRuleTest(getApacheFirewallRule(), restTemplate);

            try {
                // Wait for instances configuration start
                Thread.sleep(WAIT_MS_TO_START_INSTANCE);
            } catch (InterruptedException e) {
                e.printStackTrace();
                assertThat(e).isNull();
            }
            
            for (InstanceModel model : createdInstances) {
                responseGet = getInstanceTest(model);            
                assertThat(responseGet.getStatusCode()).isEqualTo(HttpStatus.OK);
                assertThat(responseGet.getBody()).isNotNull();            
                doHttGetInInstancesTest(responseGet.getBody());
            }
        }         
        finally{
            FirewallControllerTest.deleteRuleTest(getApacheFirewallRule(), restTemplate);                
        }
           
        for (InstanceModel model : createdInstances) {
            deleteInstanceTest(model);
        }        
        
        for (InstanceModel model : newInstances) {
            responseGet = getInstanceTest(model);
            assertThat(responseGet.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        }   

        responseList = listAllTest();        
        assertThat(responseList.size()).isEqualTo(initialSize);        
    }
    
    private void doHttGetInInstancesTest(InstanceModel model) {
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
    
    private void deleteInstanceTest(InstanceModel model) {
        
        HttpEntity<CredentialModel<Void>> entity = TestUtils.createEntity();
        
        ResponseEntity<Object> response = this.restTemplate
                .exchange(
                        Routes.INSTANCE+"/"+model.getZone() + "/"+model.getName(), 
                        HttpMethod.DELETE, 
                        entity,
                        new ParameterizedTypeReference< Object >() {}
                        );          
        assertThat(response).isNotNull();    
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
    
    private List<InstanceModel> createInstancesTest(List<InstanceModel> instances){
        HttpEntity<CredentialModel<List<InstanceModel>>> entity = 
                TestUtils.createEntity(instances);
        
        ResponseEntity<List<InstanceModel>> response = this.restTemplate
                .exchange(
                        Routes.INSTANCE, 
                        HttpMethod.POST, 
                        entity,
                        new ParameterizedTypeReference< List<InstanceModel> >() {});                     
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        
        return response.getBody();
    }
    
    private List<InstanceModel> listAllTest() {         

        HttpEntity<CredentialModel<Void>> entity = TestUtils.createEntity();
        
        ResponseEntity<List<InstanceModel>> responseList = 
                this.restTemplate
                    .exchange(
                            Routes.INSTANCES, 
                            HttpMethod.POST, 
                            entity,
                            new ParameterizedTypeReference< List<InstanceModel> >() {});          
        
        assertThat(responseList.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseList.getBody()).isNotNull();
        return responseList.getBody();
    }        
    
    private ResponseEntity<InstanceModel> getInstanceTest(InstanceModel model) {
        
        HttpEntity<CredentialModel<Void>> entity = TestUtils.createEntity();
        
        ResponseEntity<InstanceModel> response = 
                this.restTemplate
                    .exchange(
                            Routes.INSTANCE+"/"+model.getZone() + "/"+model.getName(), 
                            HttpMethod.POST, 
                            entity,
                            InstanceModel.class
                        );          
        assertThat(response).isNotNull();  
        return response;
    }
    
    private List<InstanceModel> getInstancesToCreate(int length) {
        List<InstanceModel> instances = new ArrayList<>();

        for(int i=0;i<length;i++) {
            InstanceModel instance = new InstanceModel();
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
