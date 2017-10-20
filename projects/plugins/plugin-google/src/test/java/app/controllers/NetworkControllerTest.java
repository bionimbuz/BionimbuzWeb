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
import org.springframework.http.HttpMethod;
import org.springframework.test.context.junit4.SpringRunner;

import app.common.Response;
import app.common.Routes;
import app.models.FirewallModel;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class NetworkControllerTest {

    @Autowired
    private NetworkController controller;
    @Autowired
    private TestRestTemplate restTemplate;
    @Value("${local.server.port}")
    private int PORT;

    @Test
    public void contexLoads() throws Exception {
        assertThat(controller).isNotNull();
    }

    @Test
    public void rulesTest() {

        Response<List<FirewallModel>> model = this.restTemplate
                .exchange(
                        Routes.NETWORK_RULES, 
                        HttpMethod.GET, 
                        null,
                        new ParameterizedTypeReference< Response<List<FirewallModel>> >() {})
                .getBody();

        assertThat(model.getContent()).isNotNull();
    }

}
