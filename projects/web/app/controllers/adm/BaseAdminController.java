package controllers.adm;

import controllers.BaseController;
import controllers.Secure;
import controllers.security.SecurityController;
import models.UserModel;
import play.mvc.Before;
import play.mvc.With;

@With(Secure.class)
public class BaseAdminController extends BaseController {

    protected static final String OBJECT = "object";
    
    // --------------------------------------------------------------
    // Filters
    // --------------------------------------------------------------
    @Before(priority = HIGH_PRIORITY)
    public static void setConnectedUser() {

        if (SecurityController.isConnected()) {
            renderArgs.put(CONNECTED_USER, getConnectedUser());
        }
    }

    // --------------------------------------------------------------
    // static methods
    // --------------------------------------------------------------
    public static UserModel getConnectedUser() {

        final String username = SecurityController.connected();
        if (username == null || username.isEmpty()) {
            return null;
        }
        return UserModel.findByEmail(username);
    }

}
