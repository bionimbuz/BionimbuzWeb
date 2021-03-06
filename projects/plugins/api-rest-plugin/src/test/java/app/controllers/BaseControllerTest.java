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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import app.common.GlobalConstants;
import app.common.HttpHeadersCustom;
import app.common.Routes;
import app.utils.TestUtils;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class BaseControllerTest {    
    
    @Autowired
    private TestRestTemplate restTemplate;
    @Value("${local.server.port}")
    private int PORT;
  
    @Test
    public void versionErrorTest() throws Exception {
        TestUtils.setTimeout(restTemplate.getRestTemplate(), 20000000);
        // Wrong API Version
        HttpHeaders headers = createFullHeaders(GlobalConstants.API_VERSION + ".1");   
        HttpEntity<Void> entity = 
                new HttpEntity<>(null, headers);        
        ResponseEntity<?> responseList = 
                this.restTemplate
                    .exchange(
                            Routes.IMAGES, 
                            HttpMethod.GET, 
                            entity,
                            new ParameterizedTypeReference<Object>() {});      
        assertThat(responseList.getStatusCode()).isEqualTo(HttpStatus.MOVED_PERMANENTLY);        
        
        // Right API Version
        headers = createFullHeaders(GlobalConstants.API_VERSION);   
        entity =new HttpEntity<>(null, headers);  
        responseList = 
                this.restTemplate
                    .exchange(
                            Routes.IMAGES, 
                            HttpMethod.GET, 
                            entity,
                            new ParameterizedTypeReference< String >() {});          
        assertThat(responseList.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
   
    private static HttpHeaders createFullHeaders(final String version) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);            
        headers.add(HttpHeaders.AUTHORIZATION, "authorization");
        headers.add(HttpHeadersCustom.AUTHORIZATION_ID, "identity");            
        headers.add(HttpHeadersCustom.API_VERSION, version);    
        return headers;
    }
}
