package app.utils;

import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

public class ApiTestUtils {
    
    public static void setTimeout(RestTemplate restTemplate, int timeout) {
        restTemplate.setRequestFactory(new SimpleClientHttpRequestFactory());
        SimpleClientHttpRequestFactory rf = 
                (SimpleClientHttpRequestFactory) restTemplate.getRequestFactory();
        rf.setReadTimeout(timeout);
        rf.setConnectTimeout(timeout);
    }
}
