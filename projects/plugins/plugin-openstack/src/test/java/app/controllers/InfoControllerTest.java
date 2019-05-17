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
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import app.common.Routes;
import app.common.SystemConstants;
import app.models.Body;
import app.models.PluginInfoModel;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class InfoControllerTest {

    @Autowired
    private InfoController controller;
    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void contexLoads() throws Exception {
        assertThat(controller).isNotNull();
    }

    @Test
    public void infoTest() {

    	ResponseEntity<Body<PluginInfoModel>> response = this.restTemplate
                .exchange(
                        Routes.INFO, 
                        HttpMethod.GET, 
                        null,
                        new ParameterizedTypeReference< Body<PluginInfoModel> >() {});

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getContent().getPluginVersion())
                .isEqualTo(SystemConstants.PLUGIN_VERSION);
    }

}
