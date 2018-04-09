package app.controllers;

import org.springframework.web.bind.annotation.RestController;

import app.common.SystemConstants;
import app.models.PluginInfoModel.AuthenticationType;

@RestController
public class InfoController extends AbstractInfoController {	

    /*
     * Overwritten Methods
     */
    @Override
    protected String getCloudType() {
        return SystemConstants.CLOUD_COMPUTE_TYPE;
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
        return SystemConstants.PLUGIN_NAME;
    }
    @Override
    protected String getWriteScope() {
        return SystemConstants.PLUGIN_COMPUTE_WRITE_SCOPE;
    }
    @Override
    protected String getReadScope() {
        return SystemConstants.PLUGIN_COMPUTE_READ_SCOPE;
    }
}
