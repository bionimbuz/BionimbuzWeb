package controllers.guest;

import common.constants.I18N;
import controllers.CRUD.For;
import controllers.Check;
import controllers.adm.BaseAdminController;
import models.GroupModel;
import models.UserGroupModel;
import models.UserModel;
import models.keys.UserGroupKey;
import play.data.binding.Binder;
import play.data.validation.Validation;
import play.exceptions.TemplateNotFoundException;
import play.i18n.Messages;

@For(GroupModel.class)
@Check("/groups")
public class GroupController extends BaseAdminController {
    
    private static final String SPLIT_EXP_COMMA = "(\\s*;\\s*)|(\\s*,\\s*)";
    private static final String OBJECT_STR_USERS = "object.strUsers";

    public static void create() throws Exception {
        final CustomObjectType type = CustomObjectType.get(getControllerClass());
        notFoundIfNull(type);
        final GroupModel object = new GroupModel();
        Binder.bindBean(params.getRootParamNode(), OBJECT, object);
        validation.valid(object);
        String[] strUsers = getUsersValidated(object);
        if (Validation.hasErrors()) {
            renderArgs.put("error", Messages.get(I18N.crud_hasErrors));
            try {
                render(request.controller.replace(".", "/") + "/blank.html", type, object);
            } catch (final TemplateNotFoundException e) {
                render("CRUD/blank.html", type, object);
            }
        }
        object.save();
        UserModel currentUser = getConnectedUser();
        UserGroupModel userGroup = new UserGroupModel(currentUser, object);
        userGroup.setGroup(object);
        userGroup.setUser(currentUser);
        userGroup.setOwner(true);
        userGroup.setJoined(true);
        userGroup.save();
        
        UserGroupModel.addUsersToGroup(object, strUsers);
        
        flash.success(Messages.get(I18N.crud_created, type.modelName));
        if (params.get("_save") != null) {
            redirect("guest.UserGroupController.list");
        }
        if (params.get("_saveAndAddAnother") != null) {
            redirect(request.controller + ".blank");
        }
    }
    
    public static void save(final Long id) throws Exception {
        final CustomObjectType type = CustomObjectType.get(getControllerClass());
        notFoundIfNull(type);
        final GroupModel object = GroupModel.findById(id);
        notFoundIfNull(object);
        Binder.bindBean(params.getRootParamNode(), OBJECT, object);
        validation.valid(object);
        String[] strUsers = getUsersValidated(object);
        if (Validation.hasErrors()) {
            renderArgs.put("error", Messages.get("crud.hasErrors"));
            try {
                render(request.controller.replace(".", "/") + "/show.html", type, object);
            } catch (final TemplateNotFoundException e) {
                render("CRUD/show.html", type, object);
            }
        }
        UserModel currentUser = getConnectedUser();
        UserGroupModel.addUsersToGroup(object, strUsers);      
        UserGroupModel.deleteUsersFromGroup(
                object, object.getUsersMarkedForExclusion());  
        UserGroupModel.updateGroupOwnersIgnoreCurrent(
                object, object.getUsersMarkedForOwner(), currentUser);
        object._save();
        flash.success(Messages.get("crud.saved", type.modelName));
        if (params.get("_save") != null) {
            redirect("guest.UserGroupController.list");
        }
        redirect(request.controller + ".show", object._key());
    }

    public static void show(final Long id) throws Exception {
        final CustomObjectType type = CustomObjectType.get(getControllerClass());
        notFoundIfNull(type);

        final UserModel currentUser = getConnectedUser();
        final GroupModel object = GroupModel.findById(id);        
        notFoundIfNull(object);
        final UserGroupModel userGroup = UserGroupModel.findById(
                new UserGroupKey(currentUser, object));
        notFoundIfNull(userGroup);
        try {
            render(type, object, userGroup);
        } catch (final TemplateNotFoundException e) {
            render("CRUD/show.html", type, object);
        }
    }

    private static String[] getUsersValidated(final GroupModel object) {
        String [] strUsers = {};        
        if(!object.getStrUsers().isEmpty()){ 
            strUsers = object.getStrUsers().trim().split(SPLIT_EXP_COMMA);
        }
        for(String strUser : strUsers) {
            validation.email(OBJECT_STR_USERS, strUser);
        }
        return strUsers;
    }    
}