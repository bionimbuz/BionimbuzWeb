package app.controllers;

import static org.assertj.core.api.Assertions.assertThat;

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
import app.models.PluginStorageModel;
import utils.TestUtils;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class StorageControllerTest {

    @Autowired
    private StorageController controller;
    @Autowired
    private TestRestTemplate restTemplate;
    @Value("${local.server.port}")
    private int PORT;
    private static String LOCATION = "us-central1";
    private static String BUCKET_NAME = "bionimbuz_test_us_central1";

    @Test
    public void contexLoads() throws Exception {
        assertThat(controller).isNotNull();
    }

    @Test
    public void CRUD_Test() {
        TestUtils.setTimeout(restTemplate.getRestTemplate(), 0);
        createSpaceTest();
        deleteSpaceTest();
    }

    private void createSpaceTest() {
        PluginStorageModel model = new PluginStorageModel(
                BUCKET_NAME,
                LOCATION);

        HttpEntity<PluginStorageModel> entity =
                TestUtils.createEntity(model, SystemConstants.PLUGIN_STORAGE_WRITE_SCOPE);

        ResponseEntity<Body<PluginStorageModel>> response = this.restTemplate
                .exchange(
                        Routes.SPACES,
                        HttpMethod.POST,
                        entity,
                        new ParameterizedTypeReference<Body<PluginStorageModel>>() {});
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    public void deleteSpaceTest() {

        HttpEntity<Void> entity = TestUtils.createEntity(SystemConstants.PLUGIN_STORAGE_WRITE_SCOPE);

        ResponseEntity<Body<Void>> response =
                restTemplate
                    .exchange(
                            Routes.SPACES+"/"+BUCKET_NAME,
                            HttpMethod.DELETE,
                            entity,
                            new ParameterizedTypeReference< Body<Void> >() {});
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

}
