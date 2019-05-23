package app.controllers;

import app.common.Routes;
import app.common.SystemConstants;
import app.models.Body;
import app.models.PluginComputingInstanceModel;
import app.models.PluginComputingZoneModel;
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
import utils.TestUtils;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class ComputingControllerTest {

    private static final String INSTANCE_REGION = "openstack-region";

    @Autowired
    private ComputingController controller;
    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void contexLoads() throws Exception {
        assertThat(controller).isNotNull();
    }

    @Test
    public void crud_instances() throws IOException {
        PluginComputingInstanceModel instance = createInstance();

        int current_servers = count_current_servers();
        instance = createInstanceTest(instance);
        assertThat(count_current_servers() == (current_servers + 1));
        deleteInstanceTest(instance);
        assertThat(count_current_servers() == current_servers);
    }

    @Test
    public void list_regions_test() {
        TestUtils.setTimeout(restTemplate.getRestTemplate(), 0);
        HttpEntity<Void> entity = TestUtils.createEntity(null);

        ResponseEntity<Body<List<PluginComputingZoneModel>>> responseList =
                this.restTemplate
                        .exchange(
                                Routes.COMPUTING_REGIONS_ZONES.replace("{name}", INSTANCE_REGION),
                                HttpMethod.GET,
                                entity,
                                new ParameterizedTypeReference<Body<List<PluginComputingZoneModel>>>() {
                                });

        assertThat(responseList.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseList.getBody()).isNotNull();
    }

    @Test
    public void list_regions_zones_test() {
        TestUtils.setTimeout(restTemplate.getRestTemplate(), 0);
        HttpEntity<Void> entity = TestUtils.createEntity(null);

        ResponseEntity<Body<List<PluginComputingZoneModel>>> responseList =
                this.restTemplate
                        .exchange(
                                Routes.COMPUTING_REGIONS,
                                HttpMethod.GET,
                                entity,
                                new ParameterizedTypeReference<Body<List<PluginComputingZoneModel>>>() {
                                });

        assertThat(responseList.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseList.getBody()).isNotNull();
    }



    private int count_current_servers() {
        TestUtils.setTimeout(restTemplate.getRestTemplate(), 0);

        HttpEntity<Void> entity = TestUtils.createEntity(null);

        ResponseEntity<Body<List<PluginComputingInstanceModel>>> responseList =
                this.restTemplate
                        .exchange(
                                Routes.COMPUTING_INSTANCES,
                                HttpMethod.GET,
                                entity,
                                new ParameterizedTypeReference<Body<List<PluginComputingInstanceModel>>>() {
                                });

        assertThat(responseList.getStatusCode()).isEqualTo(HttpStatus.OK);
        return responseList.getBody().getContent().size();
    }

    private PluginComputingInstanceModel createInstance() {
        PluginComputingInstanceModel instance = new PluginComputingInstanceModel();
        instance.setType(SystemConstants.CLOUD_TYPE);
        instance.setRegion(SystemConstants.PLUGIN_REGION);
        instance.setZone(SystemConstants.PLUGIN_ZONE);
        instance.setImageId("9c795320-417c-4b15-9494-918209b3edf7");
        instance.setFlavorId("1");

        return instance;
    }

    private PluginComputingInstanceModel createInstanceTest(PluginComputingInstanceModel instance) {
        HttpEntity<PluginComputingInstanceModel> entity = TestUtils.createEntity(instance);
        ResponseEntity<Body<PluginComputingInstanceModel>> response = this.restTemplate
                .exchange(
                        Routes.COMPUTING_INSTANCES,
                        HttpMethod.POST,
                        entity,
                        new ParameterizedTypeReference<Body<PluginComputingInstanceModel>>() {});
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        return response.getBody().getContent();
    }

    private void deleteInstanceTest(PluginComputingInstanceModel instance) {

        HttpEntity<PluginComputingInstanceModel> entity = TestUtils.createEntity(instance);

        ResponseEntity<Body<Boolean>> response = this.restTemplate
                .exchange(
                        Routes.COMPUTING_REGIONS_ZONES_INSTANCES_NAME
                                .replace("{region}", instance.getRegion())
                                .replace("{zone}", instance.getZone())
                                .replace("{name}", instance.getName()),
                        HttpMethod.DELETE,
                        entity,
                        new ParameterizedTypeReference<Body<Boolean>>() {
                        }
                );
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

}
