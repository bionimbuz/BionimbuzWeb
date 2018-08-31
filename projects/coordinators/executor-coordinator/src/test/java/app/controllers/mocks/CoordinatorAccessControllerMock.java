package app.controllers.mocks;

import java.util.HashMap;

import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.net.HttpHeaders;

import app.execution.jobs.IStatusRefresh;
import app.models.ExecutionStatus;
import app.models.RemoteFileInfo;
import app.security.AccessSecurity;

@RestController
public class CoordinatorAccessControllerMock {    
    
    public static final String SECRET = "Ha2uwrn1jKwmVp50BJ8K0W1LmEiuXzNnlrQuugR4ia6veGCVMMrptuMB1dljXmbU";
    private static AccessSecurity security = new AccessSecurity(
            SECRET,
            60*1000); // 60 seconds
    public static final String webDownloadUrl =      "/external/file/download/{id}";
    public static final String webUploadUrl =        "/external/file/upload/{id}";
    public static final String webRefreshTokenUrl =  "/external/token/refresh";
    public static final String webRefreshStatusUrl = "/external/status/refresh";
    
    private static final String pluginLocalBaseUrl = "http://localhost:8282";
    private String localFileDownloadPath = pluginLocalBaseUrl+"/spaces/%s/file/%s/download";
    private String localFileUploadPath = pluginLocalBaseUrl + "/spaces/%s/file/%s/upload";
    private static HashMap<Long, String> file = new HashMap<>();
    private String localFileSpace = "test";
    private IStatusRefresh statusRefresh = null;
    
    static {
        file.put(1l, "test_input1.txt");
        file.put(2l, "test_input2.txt");
        file.put(3l, "test_output1.txt");
        file.put(4l, "test_output2.txt");
    }
        
    public static String getFileNameById(long id) {
        return file.get(id);        
    }
    
    public static String generateToken(final String identity, final Long expirationTime) {
        return (new AccessSecurity(SECRET, expirationTime)).generateToken(identity);
    }
    public static void checkToken(final String token) {
        security.checkToken(token);
    }
    
    @RequestMapping(path = webDownloadUrl, method = RequestMethod.GET)
    public ResponseEntity<RemoteFileInfo> getRemoteFileInfoDownload(
            @RequestHeader(value=HttpHeaders.AUTHORIZATION) final String token,
            @PathVariable(value="id") final Long id) {
        return generateResponse(id, token, localFileDownloadPath, HttpMethod.GET.toString()); 
    }
    
    @RequestMapping(path = webUploadUrl, method = RequestMethod.GET)
    private ResponseEntity<RemoteFileInfo> getRemoteFileInfoUpload(
            @RequestHeader(value=HttpHeaders.AUTHORIZATION) final String token,
            @PathVariable(value="id") final Long id) {
        return generateResponse(id, token, localFileUploadPath, HttpMethod.POST.toString());     
    }
    
    @RequestMapping(path = webRefreshStatusUrl, method = RequestMethod.POST)
    private ResponseEntity<Void> refreshStatus(
            @RequestHeader(value=HttpHeaders.AUTHORIZATION) final String token,
            @RequestBody ExecutionStatus status) {        
        if(statusRefresh != null) {
            statusRefresh.onStatusRefreshed(
                    status.getStatus(), 
                    status.getPhase(), 
                    status.getErrorMessage());
        }
        return new ResponseEntity<>(HttpStatus.OK); 
    }    
    
    @RequestMapping(path = webRefreshTokenUrl, method = RequestMethod.GET)
    private ResponseEntity<String> refreshToken(
            @RequestHeader(value=HttpHeaders.AUTHORIZATION) final String token) {  
        try {
            String tokenRefreshed = 
                    security.refreshToken(token);
            return ResponseEntity.ok(tokenRefreshed);        
        } catch(Exception e) {
            return new ResponseEntity<>(
                    null,
                    HttpStatus.UNAUTHORIZED);
        }    
    }

    private ResponseEntity<RemoteFileInfo> generateResponse(
            final Long id, 
            final String token,
            final String url,
            final String method){
        
        String fileName = file.get(id);
        if(fileName == null || fileName.isEmpty()) {
            return new ResponseEntity<>(
                            null,
                            HttpStatus.NOT_FOUND);
        }        
        
        try {
            security.checkToken(token);
        } catch(Exception e) {
            return new ResponseEntity<>(
                    null,
                    HttpStatus.UNAUTHORIZED);
        }        
        
        RemoteFileInfo fileInfo = new RemoteFileInfo();
        fileInfo.setName(fileName);
        fileInfo.setUrl(
                String.format(
                        url,
                        localFileSpace,
                        fileName));
        fileInfo.setMethod(method);
        return ResponseEntity.ok(fileInfo);        
    }

    public void setStatusRefresh(IStatusRefresh statusRefresh) {
        this.statusRefresh = statusRefresh;
    }
}
