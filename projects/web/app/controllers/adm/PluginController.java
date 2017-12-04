package controllers.adm;

import controllers.CRUD.For;
import models.PluginModel;

@For(PluginModel.class)
public class PluginController extends BaseAdminController {

    public static String searchPlugin(final String url) {
        
        System.out.println("Searching url: " + url);
        
        return "TEST";
    }
}