package controllers.guest;

import common.utils.RandomString;
import controllers.CRUD.For;
import controllers.Check;
import controllers.adm.BaseAdminController;
import models.GroupModel;
import models.RoleModel;
import models.RoleModel.RoleType;
import models.UserGroupModel;
import models.UserModel;
import play.data.binding.Binder;
import play.data.validation.Validation;
import play.exceptions.TemplateNotFoundException;
import play.i18n.Messages;

@For(GroupModel.class)
@Check("/groups")
public class GroupController extends BaseAdminController {

    public static void create() throws Exception {
        final CustomObjectType type = CustomObjectType.get(getControllerClass());
        notFoundIfNull(type);
        final GroupModel object = new GroupModel();
        Binder.bindBean(params.getRootParamNode(), "object", object);
        validation.valid(object);
        String [] strUsers = {};        
        if(!object.getStrUsers().isEmpty()){ 
            strUsers = object.getStrUsers().split("(;)|(,)");
        }
        for(String strUser : strUsers) {
            validation.email("object.strUsers", strUser);
        }
        if (Validation.hasErrors()) {
            renderArgs.put("error", Messages.get("crud.hasErrors"));
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

        RoleModel role = RoleModel.findById(RoleType.NORMAL);
        RandomString randomStr = new RandomString(UserModel.DEFAULT_PASS_SIZE);
        for(String strUser : strUsers) {
            UserModel user = UserModel.findByEmail(strUser);
            if(user == null) {
                user = UserModel.createUser(strUser, randomStr.nextString(), role);
            }

            userGroup = new UserGroupModel(user, object);
            userGroup.setGroup(object);
            userGroup.setUser(currentUser);
            userGroup.setOwner(false);
            userGroup.setJoined(false);
            userGroup.save();            
        }
        
        flash.success(Messages.get("crud.created", type.modelName));
        if (params.get("_save") != null) {
            redirect("guest.UserGroupController.list");
        }
        if (params.get("_saveAndAddAnother") != null) {
            redirect(request.controller + ".blank");
        }
    }
}