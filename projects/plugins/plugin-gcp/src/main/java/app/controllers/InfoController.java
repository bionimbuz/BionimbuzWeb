package app.controllers;

import org.springframework.web.bind.annotation.RestController;

import app.common.SystemConstants;

@RestController
public class InfoController extends AbstractInfoController {	

    /*
     * Overwritten Methods
     */
    
    protected String getCloudType() {
        return SystemConstants.CLOUD_TYPE;
    }
    
    protected String getPluginVersion() {
        return SystemConstants.PLUGIN_VERSION;
    }
}
