package app.execution;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import app.exceptions.SingletonAlreadyInitializedException;
import app.exceptions.SingletonNotInitializedException;
import app.models.RemoteFileInfo;
import app.models.SecureFileAccess;

public class RemoteFileInfoAccess {

    private static final String HEADER_AUTHORIZATION = "authorization";
    private static final String HEADER_CONTENT_BEARER = "Bearer ";
    private static final int MAX_ATTEMPTS = 3;
    private static final int TIME_BETWEEN_ATTEMPTS = 5 * 1000;
    private SecureFileAccess secureFileAccess;
    private RestTemplate restTemplate ;

    private static RemoteFileInfoAccess inst = null;

    /*
     * Singleton Methods
     */
    
    public static void init(final SecureFileAccess secureFileAccess)
            throws SingletonAlreadyInitializedException {
        assertNotInitialized();
        inst = new RemoteFileInfoAccess(secureFileAccess);
    }
    public static RemoteFileInfoAccess get() throws SingletonNotInitializedException {
        assertInitialized();
        return inst;
    }
    public static boolean isInitialized() {
        return inst != null;        
    }
    private static void assertNotInitialized()
            throws SingletonAlreadyInitializedException {
        if(inst != null) {
            throw new SingletonAlreadyInitializedException(
                    ApplicationExecutionJob.class);
        }
    }
    private static void assertInitialized()
            throws SingletonNotInitializedException {
        if(inst == null) {
            throw new SingletonNotInitializedException(
                    ApplicationExecutionJob.class);
        }
    }
    
    /*
     * Constructors
     */
    
    private RemoteFileInfoAccess(
            final SecureFileAccess secureFileAccess) {
        this.secureFileAccess = secureFileAccess;
        this.restTemplate = new RestTemplate();
    }

    /*
     * Class methods
     */
    
    public synchronized RemoteFileInfo getRemoteFileInfo(final String remoteFilePath) {    
        
        RemoteFileInfo res = null;
        for(int i=0; i<MAX_ATTEMPTS; i++) {
            try {
                res = tryRemoteFileInfo(remoteFilePath);
                return res;
            } catch(HttpClientErrorException e) {
                if(e.getRawStatusCode() == HttpStatus.UNAUTHORIZED.value()) {
                    try {
                        refreshToken();
                    } catch(HttpClientErrorException e_refresh) {
                        if(e.getRawStatusCode() == HttpStatus.UNAUTHORIZED.value()) {
                            break;
                        }
                    }
                }                
            }            
            
            try {
                Thread.sleep(TIME_BETWEEN_ATTEMPTS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        
        return res;
    }
    
    private RemoteFileInfo tryRemoteFileInfo(final String remoteFilePath) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HEADER_AUTHORIZATION, 
                HEADER_CONTENT_BEARER + secureFileAccess.getToken());           
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        
        ResponseEntity<RemoteFileInfo> response = 
            restTemplate
                .exchange(
                        remoteFilePath, 
                        HttpMethod.GET, 
                        entity,
                        new ParameterizedTypeReference<RemoteFileInfo>() {});
        response.getStatusCodeValue();
        
        return response.getBody();        
    }
    
    private void refreshToken() {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HEADER_AUTHORIZATION, 
                HEADER_CONTENT_BEARER + secureFileAccess.getToken());           
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        
        ResponseEntity<String> response = 
            restTemplate
                .exchange(
                        secureFileAccess.getRefreshTokenUrl(), 
                        HttpMethod.GET, 
                        entity,
                        new ParameterizedTypeReference<String>() {});
        
        String token = response.getBody();
        secureFileAccess.setToken(token);      
    }
}
