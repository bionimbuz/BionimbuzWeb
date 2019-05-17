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
import org.springframework.test.context.junit4.SpringRunner;

import app.common.Routes;
import app.common.SystemConstants;
import app.models.Body;
import app.models.PluginImageModel;
import utils.TestUtils;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class ImageControllerTest {

    @Autowired
    private ImageController controller;
    @Autowired
    private TestRestTemplate restTemplate;

    private String imageId = null;

    @Test
    public void contexLoads() throws Exception {
        assertThat(controller).isNotNull();
    }

    @Test
    public void listAndGetImagesTest() {
        listImagesTest();
        getImageTest();
    }

    public void listImagesTest() {

        TestUtils.setTimeout(restTemplate.getRestTemplate(), 0);
        HttpEntity<Void> entity = TestUtils.createEntity(null);
        ResponseEntity<Body<List<PluginImageModel>>> response =
                restTemplate
                    .exchange(
                            Routes.IMAGES,
                            HttpMethod.GET,
                            entity,
                            new ParameterizedTypeReference< Body<List<PluginImageModel>> >() {});
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getContent().isEmpty()).isFalse();
        imageId = response.getBody().getContent().get(0).getId();
    }
    
    public void getImageTest() {
        TestUtils.setTimeout(restTemplate.getRestTemplate(), 0);
        HttpEntity<Void> entity = TestUtils.createEntity(null);

        ResponseEntity<Body<PluginImageModel>> response =
                this.restTemplate
                    .exchange(
                            Routes.IMAGES +"/"+ imageId,
                            HttpMethod.GET,
                            entity,
                            new ParameterizedTypeReference<Body<PluginImageModel>>() {});
        assertThat(response).isNotNull(); 
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);    
    }

}
