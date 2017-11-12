package controllers.adm;

import controllers.CRUD.For;
import models.PluginModel;

@For(PluginModel.class)
public class PluginController extends BaseAdminController {

    public static void index() {
        render();
    }

    public static void list() {
        ObjectType type = ObjectType.get(getControllerClass());
        notFoundIfNull(type);
        render(type);
    }
}