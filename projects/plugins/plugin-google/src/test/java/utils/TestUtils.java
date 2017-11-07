package utils;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Properties;

import org.jclouds.domain.Credentials;
import org.jclouds.googlecloud.GoogleCredentialsFromJson;
import org.jclouds.googlecomputeengine.GoogleComputeEngineApiMetadata;
import org.jclouds.oauth.v2.AuthorizationApi;
import org.jclouds.oauth.v2.config.OAuthProperties;
import org.jclouds.oauth.v2.domain.Claims;
import org.jclouds.oauth.v2.domain.Token;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import com.google.common.base.Supplier;
import com.google.common.io.Files;

import app.common.Authorization;
import app.common.SystemConstants;

public class TestUtils {
    protected static final Logger LOGGER = LoggerFactory.getLogger(TestUtils.class);  
    
    private static Supplier<Credentials> credentialSupplier;    
    public static final String READ_SCOPE = "https://www.googleapis.com/auth/compute.readonly"; 
    public static final String WRITE_SCOPE = "https://www.googleapis.com/auth/compute"; 
        
    static {
        String credentialContent =
                readCredential();
        credentialSupplier = 
                new GoogleCredentialsFromJson(credentialContent);
    }
    
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

        try {
            Credentials credentials = credentialSupplier.get(); 
            
            Token token = getToken(credentialSupplier, scope);
            
            System.out.println("identity: "+credentials.identity);
            System.out.println("token: "+token.accessToken());
            System.out.println("scope: "+scope);
            System.out.println("=============================");    
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);            
            headers.add("Authorization", "Bearer " + token.accessToken());
            headers.add("AuthorizationId", credentials.identity);            
   
            HttpEntity<T> entity = 
                    new HttpEntity<>(content, headers);    
            
            return entity;
        } catch (Exception e) {
            e.printStackTrace();
            assertThat(e).isNull();
            return null;
        }
    }   

    private static Token getToken(final Supplier<Credentials> credential, final String scope) throws Exception {
        
        final Properties defaultProperties = GoogleComputeEngineApiMetadata.defaultProperties();
        final String aud = defaultProperties.getProperty(OAuthProperties.AUDIENCE);
        int now = (int)ZonedDateTime.now(ZoneOffset.UTC).toEpochSecond();        

        Claims claims = Claims.create( //
                credential.get().identity, // iss
                scope, // scope
                aud, // aud
                now + 60*60, // placeholder exp for the cache
                now // placeholder iat for the cache
            );
        
        try(AuthorizationApi api = 
                Authorization.createApi(
                        credential, SystemConstants.CLOUD_TYPE)) {        
            Token token = api.authorize(claims);            
            return token;
        }
    }    
}
