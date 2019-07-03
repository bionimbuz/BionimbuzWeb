package utils;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import org.jclouds.domain.Credentials;
import org.jclouds.googlecloud.GoogleCredentialsFromJson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import com.google.common.base.Supplier;
import com.google.common.io.Files;

import app.common.Authorization;
import app.common.GlobalConstants;
import app.common.HttpHeadersCustom;
import app.common.SystemConstants;
import app.models.security.TokenModel;

public class TestUtils {
    protected static final Logger LOGGER = LoggerFactory.getLogger(TestUtils.class);  
    
    private static Supplier<Credentials> credentialSupplier;    
        
    static {
        String credentialContent =
                readCredential();
        credentialSupplier = 
                new GoogleCredentialsFromJson(credentialContent);
    }
    
    @SuppressWarnings("deprecation")
	private static String readCredential() {
        String fileContents = null;        
        try {
            fileContents = 
                    Files.toString(
                        new File(CmdLineArgs.getCredentialFile()),
                        Charset.defaultCharset());
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertThat(fileContents).isNotNull();
        return fileContents;
    }    

    public static <T> HttpEntity<T> createEntity(String scope){
        return createEntity(null, scope);
    }
    public static <T> HttpEntity<T> createEntity(T content, String scope){    
        return createEntity(content, scope, GlobalConstants.API_VERSION); 
    }
    
    public static <T> HttpEntity<T> createEntity(T content, String scope, String apiVersion){        

        try {
            Credentials credentials = credentialSupplier.get(); 
            String credential = readCredential();

            TokenModel tokenModel =
                    Authorization.getToken(
                            SystemConstants.CLOUD_COMPUTE_TYPE, 
                            scope, 
                            credential);
            
            System.out.println("identity: "+tokenModel.getIdentity());
            System.out.println("token: "+tokenModel.getToken());
            System.out.println("scope: "+scope);
            System.out.println("=============================");
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);            
            headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + tokenModel.getToken());
            headers.add(HttpHeadersCustom.AUTHORIZATION_ID, credentials.identity);
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
