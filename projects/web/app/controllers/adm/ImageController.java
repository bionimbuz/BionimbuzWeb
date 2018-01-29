package controllers.adm;

import java.io.StringWriter;
import java.lang.reflect.Constructor;
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
import play.data.binding.Binder;
import play.data.validation.Validation;
import play.db.Model;
import play.exceptions.TemplateNotFoundException;
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
    

    public static void create() throws Exception {
        final CustomObjectType type = CustomObjectType.get(getControllerClass());
        notFoundIfNull(type);
        final Constructor<?> constructor = type.entityClass.getDeclaredConstructor();
        constructor.setAccessible(true);
        final Model object = (Model) constructor.newInstance();
        Binder.bindBean(params.getRootParamNode(), "object", object);
        validation.valid(object);
        if (Validation.hasErrors()) {
            unbindFileFieldsMetadata(object);
            renderArgs.put("error", Messages.get("crud.hasErrors"));
            try {
                render(request.controller.replace(".", "/") + "/blank.html", type, object);
            } catch (final TemplateNotFoundException e) {
                render("CRUD/blank.html", type, object);
            }
        }
        bindFileFieldsMetadata(object);
        object._save();
        flash.success(Messages.get("crud.created", type.modelName));
        if (params.get("_save") != null) {
            redirect(request.controller + ".list");
        }
        if (params.get("_saveAndAddAnother") != null) {
            redirect(request.controller + ".blank");
        }
        redirect(request.controller + ".show", object._key());
    }
    
}