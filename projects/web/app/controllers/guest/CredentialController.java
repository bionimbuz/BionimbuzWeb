package controllers.guest;

import controllers.CRUD.For;
import controllers.Check;
import controllers.Secure.Security;
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
            redirect(VwCredentialController.ACTION_LIST);
        }
        redirect(request.controller + ".show", object._key());
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
            redirect(VwCredentialController.ACTION_LIST);
        }
        if (params.get("_saveAndAddAnother") != null) {
            redirect(request.controller + ".blank");
        }
        redirect(request.controller + ".show", object._key());
    }    
    
    public static void show(Long id) throws Exception {
        final CustomObjectType type = CustomObjectType.get(getControllerClass());
        notFoundIfNull(type);
        final CredentialModel object = CredentialModel.findById(id);
        notFoundIfNull(object);
        UserModel currentUser = BaseAdminController.getConnectedUser();
        if(currentUser.getId() != object.getUser().getId()) {
            Security.forbidden();
        }            
            
        try {
            unbindFileFieldsMetadata(object);
            render(type, object);
        } catch (final TemplateNotFoundException e) {
            render("CRUD/show.html", type, object);
        }
    }
    
}