package app.controllers;

import app.common.GlobalConstants;

class BaseControllerVersioned extends BaseController {
    @Override
    public String getAPIVersion() {
        return GlobalConstants.API_VERSION;
    }
}
