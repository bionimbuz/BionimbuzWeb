package app.controllers;

import static org.assertj.core.api.Assertions.assertThat;

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
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import app.common.Routes;
import app.models.Body;
import app.models.ImageModel;
import utils.TestUtils;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class ImageControllerTest {

    @Autowired
    private ImageController controller;
    @Autowired
    private TestRestTemplate restTemplate;
    @Value("${local.server.port}")
    private int PORT;
    
    private void setTimeout(RestTemplate restTemplate, int timeout) {
        //Explicitly setting ClientHttpRequestFactory instance to     
        //SimpleClientHttpRequestFactory instance to leverage 
        //set*Timeout methods
        restTemplate.setRequestFactory(new SimpleClientHttpRequestFactory());
        SimpleClientHttpRequestFactory rf = (SimpleClientHttpRequestFactory) restTemplate
                .getRequestFactory();
        rf.setReadTimeout(timeout);
        rf.setConnectTimeout(timeout);
    }

    @Test
    public void contexLoads() throws Exception {
        assertThat(controller).isNotNull();
    }

    @Test
    public void imagesTest() {        

        setTimeout(restTemplate.getRestTemplate(), 0);
        HttpEntity<Void> entity = TestUtils.createEntity(TestUtils.READ_SCOPE);        
        ResponseEntity<Body<List<ImageModel>>> response = 
                restTemplate
                    .exchange(
                            Routes.IMAGES, 
                            HttpMethod.GET, 
                            entity,
                            new ParameterizedTypeReference< Body<List<ImageModel>> >() {});          
        assertThat(response).isNotNull();    
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getContent().isEmpty()).isFalse();        
    }

}
