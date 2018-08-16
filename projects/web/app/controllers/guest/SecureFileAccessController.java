package controllers.guest;

import app.models.RemoteFileInfo;
import app.security.AccessSecurity;
import common.utils.StringUtils;
import play.Logger;
import play.Play;
import play.libs.Time;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.Http.Header;

public class SecureFileAccessController extends Controller {

    private static AccessSecurity ACCESS_CHECKER = 
            new AccessSecurity(Play.secretKey);
    private static final String CONNECTED_IDENTITY = "connectedIdentity";
    private static long EXPIRATION_TIME = 0;
    static {
        String EXPIRATION_TIME_CONF = Play.configuration.getProperty("application.token.maxAge", "0s");
        EXPIRATION_TIME = (Time.parseDuration(EXPIRATION_TIME_CONF) * 1000l);    
    }

    /*
     * Checkers
     */
    
    @Before(only = {"upload","download"})
    private static void checkToken(){
        String token = "";
        try {         
            token = getToken();   
            String identity = 
                    ACCESS_CHECKER.checkToken(token);
            renderArgs.put(CONNECTED_IDENTITY, identity);
        } catch(Exception e) {
            Logger.warn(e, "Unauthorized access attempt with token [%s] error [%s]", token, e.getMessage());
            forbidden();
        }        
    }
    
    /*
     * Actions
     */
    
    public static void download() throws Exception {    
        RemoteFileInfo fileInfo = new RemoteFileInfo();
        
        fileInfo.setMethod("GET");
        fileInfo.setName("file.txt");
        fileInfo.setUrl("http://localhost:8080");
        
        renderJSON(fileInfo);
    }
    
    public static void upload() {   
        RemoteFileInfo fileInfo = new RemoteFileInfo();
        
        fileInfo.setMethod("POST");
        fileInfo.setName("file.txt");
        fileInfo.setUrl("http://localhost:8080");
        
        renderJSON(fileInfo);    
    }
    
    public static void refreshToken() {           
        String token = "";
        try {         
            token = getToken();   
            String refreshedToken = 
                    ACCESS_CHECKER.refreshToken(token, EXPIRATION_TIME);            
            renderText(refreshedToken);
        } catch(Exception e) {
            Logger.warn(e, "Unauthorized access attempt with token [%s] error [%s]", token, e.getMessage());
            forbidden();
        }          
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
}