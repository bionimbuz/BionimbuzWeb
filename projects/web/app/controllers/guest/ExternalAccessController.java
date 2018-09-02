package controllers.guest;

import java.io.InputStreamReader;

import com.google.gson.GsonBuilder;

import app.common.utils.StringUtils;
import app.models.ExecutionStatus;
import app.models.ExecutionStatus.STATUS;
import app.models.RemoteFileInfo;
import app.security.AccessSecurity;
import models.InstanceModel;
import models.SpaceFileModel;
import play.Logger;
import play.Play;
import play.libs.Time;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.Http.Header;

public class ExternalAccessController extends Controller {

    private static AccessSecurity ACCESS_CHECKER;
    private static long EXPIRATION_TIME = 0;
    private static final String CONNECTED_IDENTITY = "connectedIdentity";
    static {
        String EXPIRATION_TIME_CONF = Play.configuration.getProperty("application.token.maxAge", "0s");
        EXPIRATION_TIME = (Time.parseDuration(EXPIRATION_TIME_CONF) * 1000l);    
        ACCESS_CHECKER = new AccessSecurity(Play.secretKey, EXPIRATION_TIME);
    }

    /*
     * Checkers
     */
    
    @Before(only = {"upload","download","refreshStatus"})
    private static void checkToken(){
        String token = "";
        try {         
            token = getToken();   
            String identity = 
                    ACCESS_CHECKER.checkToken(token);
            if(StringUtils.isEmpty(identity)) {
                throw new Exception("No identity found.");
            }
            renderArgs.put(CONNECTED_IDENTITY, identity);
        } catch(Exception e) {
            Logger.warn(e, "Unauthorized access attempt with token [%s] error [%s]", token, e.getMessage());
            forbidden();
        }        
    }
    
    /*
     * Actions
     */
    
    public static void download(final Long id) throws Exception {
        String identity = getIdentity();
        InstanceModel instance = 
                InstanceModel.findByIdentity(identity);
        if(instance == null)
            notFound("Instance not found.");
        
        if(instance.getStatus() != STATUS.RUNNING) {
            Logger.warn("File download attempt while application not running, file id [%s].", id);
            forbidden();
        }
        
        RemoteFileInfo fileInfo = 
                SpaceFileModel.getDownloadFileInfo(id);        
        renderJSON(fileInfo);
    }
    
    public static void upload(final Long id) {           
        
        String identity = getIdentity();
        InstanceModel instance = 
                InstanceModel.findByIdentity(identity);
        if(instance == null)
            notFound("Instance not found.");

        SpaceFileModel file = SpaceFileModel.findById(id);
        if(file == null)
            notFound("Space file not found.");
        
        if(instance.getStatus() != STATUS.RUNNING) {
            Logger.warn("File upload attempt while application not running, file id [%s].", id);
            forbidden();
        }
        
        RemoteFileInfo fileInfo = 
                SpaceFileModel.getUploadFileInfo(file.getSpace(), file.getVirtualName());        
        
        renderJSON(fileInfo);
    }
    
    public static void refreshToken() {           
        String token = "";
        try {         
            token = getToken();   
            String refreshedToken = 
                    ACCESS_CHECKER.refreshToken(token);            
            renderText(refreshedToken);
        } catch(Exception e) {
            Logger.warn(e, "Unauthorized access attempt with token [%s] error [%s]", token, e.getMessage());
            forbidden();
        }          
    }    

    public static void refreshStatus() {
        String identity = getIdentity();
        InstanceModel instance = 
                InstanceModel.findByIdentity(identity);
        if(instance == null)
            notFound("Instance not found.");
        if(instance.getStatus() == STATUS.STOPPED 
            || instance.getStatus() == STATUS.FINISHED) {
            Logger.warn("Attempt to refresh a status already stopped, instance [%s] ", instance.getId());
            badRequest("Status cannot be updated.");
        }
        
        ExecutionStatus status = new GsonBuilder().create().fromJson(
                new InputStreamReader(request.body), ExecutionStatus.class);
        instance.setStatus(status.getStatus());
        instance.setPhase(status.getPhase());
        instance.setExecutionObservation(status.getErrorMessage());
        instance.save();
    }
    
    public static String generateToken(final String identity) {
        if(StringUtils.isEmpty(identity))
            return null; 
        return ACCESS_CHECKER.generateToken(identity);
    }

    /*
     * Private methods
     */
    
    private static String getToken() throws Exception{
        Header token = 
                request.headers.get("authorization");
        
        if(token == null || StringUtils.isEmpty(token.value())){
            throw new Exception("No authorization token found in request");
        }
        return token.value();
    }    
    
    private static String getIdentity() {
        return renderArgs.get(CONNECTED_IDENTITY).toString();
    }
}