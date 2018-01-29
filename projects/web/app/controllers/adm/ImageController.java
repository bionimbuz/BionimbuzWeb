package controllers.adm;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;

import app.client.ImageApi;
import app.common.Authorization;
import app.models.Body;
import app.models.security.TokenModel;
import common.constants.I18N;
import controllers.CRUD.For;
import controllers.Check;
import models.CredentialModel;
import models.ImageModel;
import models.PluginModel;
import play.i18n.Messages;

@For(ImageModel.class)
@Check("/adm/images")
public class ImageController extends BaseAdminController {
    
    public static void searchImages(final Long pluginId) {
        try {
            PluginModel plugin = PluginModel.findById(pluginId);
            if(plugin == null)
                notFound(Messages.get(I18N.plugin_not_found));

            List<ImageModel> listModels = new ArrayList<ImageModel>();
            ImageApi imageApi = new ImageApi(plugin.getUrl());  
            
            for(CredentialModel credential : plugin.getListCredentials()) {
                try {
                    StringWriter writer = new StringWriter();
                    IOUtils.copy(credential.getCredentialData().get(), writer, "UTF-8");
                    
                    TokenModel token = Authorization.getToken(
                            plugin.getCloudType(),
                            plugin.getReadScope(),
                            writer.toString());
                    Body<List<app.models.ImageModel>> body = 
                            imageApi.listImages(
                                    token.getToken(), 
                                    token.getIdentity());       
                    if(body.getContent() == null || body.getContent().isEmpty())
                        continue;                    
                    for(app.models.ImageModel image : body.getContent()) {
                        listModels.add(new ImageModel(
                                image.getName(),
                                image.getUrl()));
                    }                    
                } catch (Exception e) {
                    e.printStackTrace();                    
                }
            } 

            renderJSON(listModels);            
        } catch (Exception e) {
            e.printStackTrace();
            notFound(Messages.get(I18N.plugin_not_found));
        }
    }    
}