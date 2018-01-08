package controllers.adm;

import app.client.InfoApi;
import app.client.PluginApi;
import app.models.Body;
import app.models.InfoModel;
import common.constants.I18N;
import controllers.CRUD.For;
import controllers.Check;
import models.PluginModel;
import play.i18n.Messages;
import retrofit2.Call;

@For(PluginModel.class)
@Check("/adm/plugins")
public class PluginController extends BaseAdminController {

    public static void searchPlugin(final String url) {
        try {
            PluginApi pluginApi = new PluginApi(url);
            InfoApi infoApi = pluginApi.createApi(InfoApi.class);  
            Call<Body<InfoModel>> call = infoApi.getInfo();        
            Body<InfoModel> body = call.execute().body();
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