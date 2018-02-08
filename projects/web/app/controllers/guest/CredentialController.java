package controllers.guest;

import java.util.List;

import org.apache.commons.lang.StringUtils;

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
    
    private static final String OBJECT_LISTSHAREDGROUPS_ID = "object.listSharedGroups.id";

    public static void save(String id) throws Exception {
        final CustomObjectType type = CustomObjectType.get(getControllerClass());
        notFoundIfNull(type);
        final Model object = type.findById(id);
        notFoundIfNull(object);
        // Treatment for multiselect empty
        if(params.get(OBJECT_LISTSHAREDGROUPS_ID) == null) {
            params.put(OBJECT_LISTSHAREDGROUPS_ID, "");
        }
        Binder.bindBean(params.getRootParamNode(), "object", object);
        validation.valid(object);
        if (Validation.hasErrors()) {
            unbindFileFieldsMetadata(object);
            renderArgs.put("error", Messages.get("crud.hasErrors"));
            try {
                render(request.controller.replace(".", "/") + "/show.html", type, object);
            } catch (final TemplateNotFoundException e) {
                render("CRUD/show.html", type, object);
            }
        }
        bindFileFieldsMetadata(object);
        object._save();
        flash.success(Messages.get("crud.saved", type.modelName));
        if (params.get("_save") != null) {
            redirect(request.controller + ".list");
        }
        redirect(request.controller + ".show", object._key());
    }
    
    public static void list(final Integer pluginSelected, int page, String search, String searchFields, String orderBy, String order) {
        final CustomObjectType type = CustomObjectType.get(getControllerClass());
        notFoundIfNull(type);
        if (page < 1) {
            page = 1;
        }
        
        String where = (String) request.args.getOrDefault("where", "");
        if(!StringUtils.isEmpty(where))
            where += " AND ";
        UserModel currentUser = BaseAdminController.getConnectedUser();
        where += " user.id = " + currentUser.getId();        
        if(pluginSelected != null) {
            where += " AND plugin.id = " + pluginSelected;
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