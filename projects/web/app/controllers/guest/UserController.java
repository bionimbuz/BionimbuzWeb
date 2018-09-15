package controllers.guest;

import app.common.utils.StringUtils;
import controllers.CRUD.For;
import controllers.Check;
import controllers.adm.BaseAdminController;
import controllers.adm.HomeController;
import controllers.security.SecurityController;
import models.UserModel;
import play.data.binding.Binder;
import play.data.validation.Validation;
import play.exceptions.TemplateNotFoundException;
import play.i18n.Messages;

@For(UserModel.class)
@Check("/list/users")
public class UserController extends BaseAdminController {

    public static void show() throws Exception {
        final CustomObjectType type = CustomObjectType.get(getControllerClass());
        notFoundIfNull(type);
        final UserModel object = getConnectedUser();
        notFoundIfNull(object);
        try {
            unbindFileFieldsMetadata(object);
            render(type, object);
        } catch (final TemplateNotFoundException e) {
            render("CRUD/show.html", type, object);
        }
    }
    
    public static void save() throws Exception {
        final CustomObjectType type = CustomObjectType.get(getControllerClass());
        notFoundIfNull(type);
        final UserModel object = getConnectedUser();
        notFoundIfNull(object);
        Binder.bindBean(params.getRootParamNode(), "object", object);
        validation.valid(object);
        if (Validation.hasErrors()) {
            renderArgs.put("error", Messages.get("crud.hasErrors"));
            try {
                render(request.controller.replace(".", "/") + "/show.html", type, object);
            } catch (final TemplateNotFoundException e) {
                redirect(HomeController.ADM_HOME_CONTROLLER_INDEX);
            }
        }
        object._save();
        flash.success(Messages.get("crud.saved", type.modelName));
        if (params.get("_save") != null) {
            redirect(HomeController.ADM_HOME_CONTROLLER_INDEX);
        }
        redirect(request.controller + ".show");
    }

    public static void showPassword() {
        final CustomObjectType type = CustomObjectType.get(getControllerClass());
        notFoundIfNull(type);
        final UserModel object = getConnectedUser();
        notFoundIfNull(object);
        try {
            render(type);
        } catch (final TemplateNotFoundException e) {
            render("CRUD/showPassword.html", type, object);
        }
    }    

    public static void savePassword(
            final String passwd, 
            final String passwdConfirmation) throws Exception {
        final CustomObjectType type = CustomObjectType.get(getControllerClass());
        notFoundIfNull(type);
        final UserModel object = getConnectedUser();
        notFoundIfNull(object);
        
        if(StringUtils.isEmpty(passwd) && 
                StringUtils.isEmpty(passwdConfirmation)) {
            redirect(HomeController.ADM_HOME_CONTROLLER_INDEX);
        }
        
        if(!passwd.equals(passwdConfirmation)) {
            validation.addError("passwdConfirmation", "Password confirmation do not match.");            
            renderArgs.put("error", Messages.get("crud.hasErrors"));
            try {
                render(request.controller.replace(".", "/") + "/showPassword.html", type, object);
            } catch (final TemplateNotFoundException e) {
                redirect(HomeController.ADM_HOME_CONTROLLER_INDEX);
            }
        }
        
        validation.minSize(passwd, 5);        
        if (Validation.hasErrors()) {
            renderArgs.put("error", Messages.get("crud.hasErrors"));
            try {
                render(request.controller.replace(".", "/") + "/showPassword.html", type, object);
            } catch (final TemplateNotFoundException e) {
                redirect(HomeController.ADM_HOME_CONTROLLER_INDEX);
            }
        }
        
        object.setPass(SecurityController.getSHA512(passwd));
        object._save();
        flash.success(Messages.get("crud.saved", type.modelName));
        redirect(HomeController.ADM_HOME_CONTROLLER_INDEX);
    }
    
}