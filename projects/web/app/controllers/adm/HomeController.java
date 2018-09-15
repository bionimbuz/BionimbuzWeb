package controllers.adm;

public class HomeController extends BaseAdminController {

    public static final String ADM_HOME_CONTROLLER_INDEX = "adm.HomeController.index";

    public static void index() {
        render();
    }
}