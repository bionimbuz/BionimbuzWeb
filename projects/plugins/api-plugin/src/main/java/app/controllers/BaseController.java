package app.controllers;

import app.common.GlobalConstants;
import app.common.exceptions.VersionException;

public class BaseController {
    
    public static void assertVersionSupported(final String version) throws VersionException {
        if(version.equals(GlobalConstants.API_VERSION))
            return;
        throw new VersionException("Resquested plugin version [" + version + "]"
                + " is different from this plugin version [" + GlobalConstants.API_VERSION + "]");
    }

}
