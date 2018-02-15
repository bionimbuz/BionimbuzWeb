package controllers.guest;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import controllers.CRUD.For;
import controllers.Check;
import controllers.adm.BaseAdminController;
import models.UserModel;
import models.VwCredentialModel;
import play.db.Model;
import play.exceptions.TemplateNotFoundException;

@For(VwCredentialModel.class)
@Check("/list/credentials")
public class VwCredentialController extends BaseAdminController {
    
    public static String ACTION_LIST = "guest.VwCredentialController.list"; 
    
    public static void list(int page, String search, String searchFields, String orderBy, String order) {
        final CustomObjectType type = CustomObjectType.get(getControllerClass());
        notFoundIfNull(type);
        if (page < 1) {
            page = 1;
        }
        
        String where = (String) request.args.getOrDefault("where", "");
        if(!StringUtils.isEmpty(where))
            where += " AND ";
        UserModel currentUser = BaseAdminController.getConnectedUser();
        where += " (userShared.id = " + currentUser.getId()+")";     
        
        
        final List<Model> objects = type.findPage(page, search, searchFields, orderBy, order, where);
        final Long count = type.count(search, searchFields, where);
        final Long totalCount = type.count(null, null, where);
        try {
            render(type, objects, count, totalCount, page, orderBy, order);
        } catch (final TemplateNotFoundException e) {
            render("CRUD/list.html", type, objects, count, totalCount, page, orderBy, order);
        }
    }

    public static void show(final Long id) throws Exception {
        final CustomObjectType type = CustomObjectType.get(getControllerClass());
        notFoundIfNull(type);
        final VwCredentialModel object = VwCredentialModel.find("id = ?1", id).first();
        notFoundIfNull(object);
        try {
            unbindFileFieldsMetadata(object);
            render(type, object);
        } catch (final TemplateNotFoundException e) {
            render("CRUD/show.html", type, object);
        }
    }
}