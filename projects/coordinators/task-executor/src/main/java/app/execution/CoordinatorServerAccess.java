package app.execution;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import app.exceptions.SingletonAlreadyInitializedException;
import app.exceptions.SingletonNotInitializedException;
import app.execution.jobs.ApplicationExecutionJob;
import app.models.ExecutionStatus;
import app.models.RemoteFileInfo;
import app.models.SecureCoordinatorAccess;

public class CoordinatorServerAccess {
    protected static final Logger LOGGER = LoggerFactory.getLogger(CoordinatorServerAccess.class);

    private static final String HEADER_AUTHORIZATION = "authorization";
    private static final String HEADER_CONTENT_BEARER = "Bearer ";
    private static final int MAX_ATTEMPTS = 3;
    private static final int TIME_BETWEEN_ATTEMPTS = 5 * 1000;
    private String refreshStatusUrl;
    private SecureCoordinatorAccess secureAccess;
    private RestTemplate restTemplate ;

    private static CoordinatorServerAccess inst = null;

    /*
     * Singleton Methods
     */
    
    public static void init(final String refreshStatusUrl, final SecureCoordinatorAccess secureAccess)
            throws SingletonAlreadyInitializedException {
        assertNotInitialized();
        inst = new CoordinatorServerAccess(refreshStatusUrl, secureAccess);
    }
    public static CoordinatorServerAccess get() throws SingletonNotInitializedException {
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
    
    private CoordinatorServerAccess(
            final String refreshStatusUrl, 
            final SecureCoordinatorAccess secureFileAccess) {
        this.refreshStatusUrl = refreshStatusUrl;
        this.secureAccess = secureFileAccess;
        this.restTemplate = new RestTemplate();
    }

    /*
     * Class methods
     */
    
    public synchronized RemoteFileInfo getRemoteFileInfo(final String remoteFilePath) {    
        
        RemoteFileInfo res = null;
        for(int i=0; i<MAX_ATTEMPTS; i++) {
            try {
                res = tryGetRemoteFileInfo(remoteFilePath);
                return res;
            } catch(HttpStatusCodeException e) {
                if(e.getRawStatusCode() == HttpStatus.UNAUTHORIZED.value()) {
                    try {
                        refreshToken();
                    } catch(HttpStatusCodeException e_refresh) {
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
    
    public synchronized void sendUpdateStatus() { 
        while(true) {
            try {
                tryUpdateStatus();
                break;
            } catch(HttpStatusCodeException e) {
                if(e.getRawStatusCode() == HttpStatus.UNAUTHORIZED.value()) {
                    try {
                        refreshToken();
                    } catch(HttpStatusCodeException e_refresh) {
                        if(e.getRawStatusCode() == HttpStatus.UNAUTHORIZED.value()) {
                            break;
                        }
                    }
                }                
            } catch (SingletonNotInitializedException e) {
                LOGGER.error(e.getMessage(), e);
                break;
            }            
            
            try {
                Thread.sleep(TIME_BETWEEN_ATTEMPTS);
            } catch (InterruptedException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
    }
        
    private RemoteFileInfo tryGetRemoteFileInfo(final String remoteFilePath) {
        HttpHeaders headers = generateHeaders();           
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
        
    private void tryUpdateStatus() throws 
                SingletonNotInitializedException {
        
        HttpHeaders headers = generateHeaders();                   
        HttpEntity<ExecutionStatus> entity = new HttpEntity<>(
                ApplicationExecutionJob.get().getExecutionStatus(), 
                headers); 
        
        ResponseEntity<Void> response = 
            restTemplate
                .exchange(
                        refreshStatusUrl, 
                        HttpMethod.POST, 
                        entity,
                        new ParameterizedTypeReference<Void>() {});
        response.getStatusCodeValue();
    }
    
    private void refreshToken() {
        HttpHeaders headers = generateHeaders();           
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        
        ResponseEntity<String> response = 
            restTemplate
                .exchange(
                        secureAccess.getRefreshTokenUrl(), 
                        HttpMethod.GET, 
                        entity,
                        new ParameterizedTypeReference<String>() {});
        
        String token = response.getBody();
        secureAccess.setToken(token);      
    }
    
    private HttpHeaders generateHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HEADER_AUTHORIZATION, 
                HEADER_CONTENT_BEARER + secureAccess.getToken());
        return headers;
    }
}
