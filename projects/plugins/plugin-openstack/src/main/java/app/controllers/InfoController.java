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
        return SystemConstants.CLOUD_TYPE;
    }
    @Override
    protected String getPluginVersion() {
        return SystemConstants.PLUGIN_VERSION;
    }
    @Override
    protected AuthenticationType getAuthenticationType() {
        return AuthenticationType.AUTH_SUPER_USER;
    }
    @Override
    protected String getName() {
        return SystemConstants.PLUGIN_NAME;
    }
    @Override
    protected String getInstanceWriteScope() { return ""; }
    @Override
    protected String getInstanceReadScope() { return ""; }
    @Override
    protected String getStorageWriteScope() { return ""; }
    @Override
    protected String getStorageReadScope() { return ""; }
}
