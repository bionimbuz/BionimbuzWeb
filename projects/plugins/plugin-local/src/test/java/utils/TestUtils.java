package utils;

import static org.assertj.core.api.Assertions.assertThat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import app.common.GlobalConstants;
import app.common.HttpHeadersCustom;

public class TestUtils {
    protected static final Logger LOGGER = LoggerFactory.getLogger(TestUtils.class);  
    
    public static String getUrl(int port) {
        return "http://localhost:"+port;
    }
    
    public static <T> HttpEntity<T> createEntity(String scope){
        return createEntity(null, scope);
    }
    public static <T> HttpEntity<T> createEntity(T content, String scope){    
        return createEntity(content, scope, GlobalConstants.API_VERSION); 
    }
    
    public static <T> HttpEntity<T> createEntity(T content, String scope, String apiVersion){        

        try {            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);            
            headers.add(HttpHeaders.AUTHORIZATION, "");
            headers.add(HttpHeadersCustom.AUTHORIZATION_ID, "");
            headers.add(HttpHeadersCustom.API_VERSION, apiVersion);            
   
            HttpEntity<T> entity = 
                    new HttpEntity<>(content, headers);    
            
            return entity;
        } catch (Exception e) {
            e.printStackTrace();
            assertThat(e).isNull();
            return null;
        }
    }   
    
    public static void setTimeout(RestTemplate restTemplate, int timeout) {
        restTemplate.setRequestFactory(new SimpleClientHttpRequestFactory());
        SimpleClientHttpRequestFactory rf = 
                (SimpleClientHttpRequestFactory) restTemplate.getRequestFactory();
        rf.setReadTimeout(timeout);
        rf.setConnectTimeout(timeout);
    }
}
