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
import org.springframework.test.context.junit4.SpringRunner;

import app.common.Response;
import app.common.Response.Type;
import app.common.Routes;
import app.common.SystemConstants;
import app.models.InfoModel;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class InfoControllerTest {

    @Autowired
    private InfoController controller;
    @Autowired
    private TestRestTemplate restTemplate;
    @Value("${local.server.port}")
    private int PORT;

    @Test
    public void contexLoads() throws Exception {
        assertThat(controller).isNotNull();
    }

    @Test
    public void infoTest() {

        Response<InfoModel> response = this.restTemplate
                .exchange(
                        Routes.INFO, 
                        HttpMethod.GET, 
                        null,
                        new ParameterizedTypeReference<Response<InfoModel>>() {})
                .getBody();

        assertThat(response.getType()).isEqualTo(Type.SUCCESS);
        assertThat(response.getContent()).isNotNull();
        assertThat(response.getContent().getVersion())
                .isEqualTo(SystemConstants.SYSTEM_VERSION);
    }

}
