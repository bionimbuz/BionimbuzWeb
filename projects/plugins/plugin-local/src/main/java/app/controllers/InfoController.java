package app.controllers;

import java.net.InetAddress;
import java.net.UnknownHostException;

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
        return AuthenticationType.AUTH_SUPER_USER;
    }
    @Override
    protected String getName() {
        return getHostName();
    }
    @Override
    protected String getInstanceWriteScope() {
        return "";
    }
    @Override
    protected String getInstanceReadScope() {
        return "";
    }
    @Override
    protected String getStorageWriteScope() {
        return "";
    }
    @Override
    protected String getStorageReadScope() {
        return "";
    }

    public static String getHostName() {
        InetAddress inetAddress;
        try {
            inetAddress = InetAddress.getLocalHost();
            return inetAddress.getHostName();
        } catch (UnknownHostException e) {
            return SystemConstants.PLUGIN_NAME;
        }
    }
}
