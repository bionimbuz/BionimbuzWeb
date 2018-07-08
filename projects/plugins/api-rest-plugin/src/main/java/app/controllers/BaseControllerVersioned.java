package app.controllers;

import app.common.GlobalConstants;

public class BaseControllerVersioned extends BaseController {
    @Override
    public String getAPIVersion() {
        return GlobalConstants.API_VERSION;
    }
}
