package app.controllers;

import app.common.GlobalConstants;

class BaseControllerVersioned extends BaseController {

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // * @see app.common.IVersion#getAPIVersion()
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    @Override
    public String getAPIVersion() {
        return GlobalConstants.API_VERSION;
    }
}
