package app.controllers;

import org.springframework.web.bind.annotation.RestController;

import app.common.SystemConstants;
import app.models.InfoModel.AuthenticationType;

@RestController
public class InfoController extends AbstractInfoController {	

    /*
     * Overwritten Methods
     */
    @Override
    protected String getCloudType() {
        return SystemConstants.CLOUD_TYPE;
    }
    @Override
    protected String getPluginVersion() {
        return SystemConstants.PLUGIN_VERSION;
    }    
    @Override
    protected AuthenticationType getAuthenticationType() {
        return AuthenticationType.AUTH_BEARER_TOKEN;
    }
    @Override
    protected String getName() {
        return "Google Cloud Platform";
    }
}
