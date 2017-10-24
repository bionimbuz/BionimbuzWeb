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
import app.common.TestUtils;
import app.models.CredentialModel;
import app.models.InstanceCreationModel;
import app.models.InstanceModel;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class InstanceControllerTest {

    private static final String INSTANCE_ZONE = "us-east1-b";
    private static final String INSTANCE_REGION = "us-east1";
    private static final String INSTANCE_TYPE = "f1-micro";
    private static final String INSTANCE_STARTUP_SCRIPT = "apt-get update && apt-get install -y apache2 && hostname > /var/www/index.html";
    private static final String INSTANCE_IMAGE_URL = "https://www.googleapis.com/compute/v1/projects/ubuntu-os-cloud/global/images/ubuntu-1604-xenial-v20170919";
    
    @Autowired
    private InstanceController controller;
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
        List<InstanceModel> responseList = listAllTest();
        
        List<InstanceCreationModel> newInstances = getInstancesToCreate(3);
        
        HttpEntity<CredentialModel<List<InstanceCreationModel>>> entity = 
                TestUtils.createEntity(newInstances);
        
        ResponseEntity<Object> response = this.restTemplate
                .exchange(
                        Routes.FIREWALL, 
                        HttpMethod.POST, 
                        entity,
                        new ParameterizedTypeReference< Object >() {});                     
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
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
    
    private List<InstanceCreationModel> getInstancesToCreate(int length) {
        List<InstanceCreationModel> instances = new ArrayList<>();

        for(int i=0;i<length;i++) {
            InstanceCreationModel instance = new InstanceCreationModel();
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
