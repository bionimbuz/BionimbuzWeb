package controllers.adm;

import app.client.InfoApi;
import app.models.Body;
import app.models.PluginInfoModel;
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
            Body<PluginInfoModel> body = infoApi.getInfo();
            PluginInfoModel info = body.getContent();

            PluginModel model = new PluginModel();
            model.setUrl(url);
            model.setAuthType(info.getAuthType());
            model.setCloudType(info.getCloudType());
            model.setName(info.getName());
            model.setPluginVersion(info.getPluginVersion());
            model.setInstanceReadScope(info.getInstanceReadScope());
            model.setInstanceWriteScope(info.getInstanceWriteScope());
            model.setStorageReadScope(info.getStorageReadScope());
            model.setStorageWriteScope(info.getStorageWriteScope());
            renderJSON(model);
        } catch (Exception e) {
            e.printStackTrace();
            notFound(Messages.get(I18N.plugin_not_found));
        }
    }
}