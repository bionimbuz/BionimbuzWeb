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
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import app.common.GlobalConstants;
import app.common.HttpHeadersCustom;
import app.common.Routes;
import app.models.Body;
import app.models.PricingModel;
import utils.TestUtils;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class PricingControllerTest {

    @Autowired
    private PricingController controller;
    @Autowired
    private TestRestTemplate restTemplate;
    @Value("${local.server.port}")
    private int PORT;

    @Test
    public void contexLoads() throws Exception {
        assertThat(controller).isNotNull();
    }

    @Test
    public void getPricingTest() {

        TestUtils.setTimeout(restTemplate.getRestTemplate(), 0);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeadersCustom.API_VERSION, GlobalConstants.API_VERSION);           
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        
        ResponseEntity<Body<PricingModel>> response = 
                this.restTemplate
                    .exchange(
                            Routes.PRICING, 
                            HttpMethod.GET, 
                            entity,
                            new ParameterizedTypeReference<Body<PricingModel>>() {});     
        response = 
                this.restTemplate
                    .exchange(
                            Routes.PRICING, 
                            HttpMethod.GET, 
                            entity,
                            new ParameterizedTypeReference<Body<PricingModel>>() {});   
        assertThat(response).isNotNull();  
    }
}
