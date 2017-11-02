package app.controllers;

import app.common.SystemConstants;

public class InfoController {	
    
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
