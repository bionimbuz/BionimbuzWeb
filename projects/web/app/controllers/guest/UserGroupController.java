package controllers.guest;

import java.util.List;

import controllers.CRUD.For;
import controllers.Check;
import controllers.adm.BaseAdminController;
import models.UserGroupModel;
import play.db.Model;
import play.exceptions.TemplateNotFoundException;

@For(UserGroupModel.class)
@Check("/list/user/groups")
public class UserGroupController extends BaseAdminController {
    
    public static String ACTION_LIST = "guest.UserGroupController.list"; 
    
    public static void list(int page, String search, String searchFields, String orderBy, String order) {
        final CustomObjectType type = CustomObjectType.get(getControllerClass());
        notFoundIfNull(type);
        if (page < 1) {
            page = 1;
        }
        
        String where = (String) request.args.get("where");
        if(where != null && !where.isEmpty())
            where += " AND ";
        else
            where = "";      
        where += " user.id = "  + getConnectedUser().getId();            
            
        final List<Model> objects = type.findPage(page, search, searchFields, orderBy, order, where);
        final Long count = type.count(search, searchFields, where);
        final Long totalCount = type.count(null, null, where);
        try {
            render(type, objects, count, totalCount, page, orderBy, order);
        } catch (final TemplateNotFoundException e) {
            render("CRUD/list.html", type, objects, count, totalCount, page, orderBy, order);
        }
    }
}