package controllers.adm;

import org.apache.commons.lang.StringUtils;

import controllers.CRUD.For;
import controllers.Check;
import models.CoordinatorModel;
import play.data.binding.Binder;
import play.data.validation.Validation;
import play.exceptions.TemplateNotFoundException;
import play.i18n.Messages;

@For(CoordinatorModel.class)
@Check("/adm/coordinator")
public class CoordinatorController extends BaseAdminController {
    
    private static final String OBJECT_LISTIMAGES = "object.listImages.id"; 

    public static void show() throws Exception {
        final CustomObjectType type = CustomObjectType.get(getControllerClass());
        notFoundIfNull(type);
        CoordinatorModel object = CoordinatorModel.first();
        if(object == null) {
            object = new CoordinatorModel();
        }
        try {
            render(type, object);
        } catch (final TemplateNotFoundException e) {
            render("CRUD/show.html", type, object);
        }
    }
    
    public static void save(final String coordinatorId) throws Exception {        
        final CustomObjectType type = CustomObjectType.get(getControllerClass());
        notFoundIfNull(type);
        CoordinatorModel object = null;    
        if(StringUtils.isEmpty(coordinatorId)) {
            object = new CoordinatorModel();    
        } else {
            object = CoordinatorModel.findById(Long.parseLong(coordinatorId));
            notFoundIfNull(object);            
        }
        
        // Treatment for multiselect empty
        if(params.get(OBJECT_LISTIMAGES) == null) {
            params.put(OBJECT_LISTIMAGES, "");
        }
        
        Binder.bindBean(params.getRootParamNode(), "object", object);
        validation.valid(object);
        if (Validation.hasErrors()) {
            renderArgs.put("error", Messages.get("crud.hasErrors"));
            try {
                render(request.controller.replace(".", "/") + "/show.html", type, object);
            } catch (final TemplateNotFoundException e) {
                render("CRUD/show.html", type, object);
            }
        }
        object._save();
        flash.success(Messages.get("crud.saved", type.modelName));
        redirect(request.controller + ".show", object._key());        
    }
    
}