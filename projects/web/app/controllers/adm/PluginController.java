package controllers.adm;

import app.client.InfoApi;
import app.models.Body;
import app.models.InfoModel;
import common.constants.I18N;
import controllers.CRUD.For;
import controllers.Check;
import models.PluginModel;
import play.i18n.Messages;

@For(PluginModel.class)
@Check("/adm/list/plugins")
public class PluginController extends BaseAdminController {

    public static void searchPlugin(final String url) {
        try {
            InfoApi infoApi = new InfoApi(url);    
            Body<InfoModel> body = infoApi.getInfo();        
            InfoModel info = body.getContent();
            
            PluginModel model = new PluginModel();
            model.setUrl(url);
            model.setAuthType(info.getAuthType());
            model.setCloudType(info.getCloudType());
            model.setName(info.getName());
            model.setPluginVersion(info.getPluginVersion());    
            model.setReadScope(info.getReadScope());
            model.setWriteScope(info.getWriteScope());
            renderJSON(model);            
        } catch (Exception e) {
            e.printStackTrace();
            notFound(Messages.get(I18N.plugin_not_found));
        }
    }
}