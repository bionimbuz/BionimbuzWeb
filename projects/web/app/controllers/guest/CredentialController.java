package controllers.guest;

import java.util.List;

import controllers.CRUD.For;
import controllers.Check;
import controllers.adm.BaseAdminController;
import models.CredentialModel;
import models.UserModel;
import play.data.binding.Binder;
import play.data.validation.Validation;
import play.db.Model;
import play.exceptions.TemplateNotFoundException;
import play.i18n.Messages;

@For(CredentialModel.class)
@Check("/list/credentials")
public class CredentialController extends BaseAdminController {
    
    public static void list(final Integer pluginSelected, int page, String search, String searchFields, String orderBy, String order) {
        final CustomObjectType type = CustomObjectType.get(getControllerClass());
        notFoundIfNull(type);
        if (page < 1) {
            page = 1;
        }
        
        String where = (String) request.args.get("where");
        
        if(pluginSelected != null) {
            if(where != null && !where.isEmpty())
                where += " AND ";
            else
                where = "";
            where += " plugin.id = " + pluginSelected;
        }   
        
        final List<Model> objects = type.findPage(page, search, searchFields, orderBy, order, where);
        final Long count = type.count(search, searchFields, where);
        final Long totalCount = type.count(null, null, where);
        try {
            render(pluginSelected, type, objects, count, totalCount, page, orderBy, order);
        } catch (final TemplateNotFoundException e) {
            render("CRUD/list.html", type, objects, count, totalCount, page, orderBy, order);
        }
    }

    public static void create() throws Exception {
        final CustomObjectType type = CustomObjectType.get(getControllerClass());
        notFoundIfNull(type);
        final CredentialModel object = new CredentialModel();        
        UserModel currentUser = BaseAdminController.getConnectedUser();
        object.setUser(currentUser);        
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