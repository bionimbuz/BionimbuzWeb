package controllers.guest;

import java.util.Date;

import controllers.CRUD.For;
import controllers.Check;
import controllers.adm.BaseAdminController;
import models.WorkflowModel;
import models.WorkflowModel.WORKFLOW_STATUS;
import models.WorkflowNodeModel;
import play.data.binding.Binder;
import play.data.validation.Validation;
import play.exceptions.TemplateNotFoundException;
import play.i18n.Messages;

@For(WorkflowModel.class)
@Check("/list/workflows")
public class WorkflowController extends BaseAdminController {
    
    public static void save(final Long id) throws Exception {
        final CustomObjectType type = CustomObjectType.get(getControllerClass());
        notFoundIfNull(type);
        final WorkflowModel object = WorkflowModel.findById(id);
        notFoundIfNull(object);
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
        if (params.get("_save") != null) {
            redirect(request.controller + ".list");
        }
        redirect(request.controller + ".show", object._key());
    }
    
    public static void create() throws Exception {
        final CustomObjectType type = CustomObjectType.get(getControllerClass());
        notFoundIfNull(type);
        final WorkflowModel object = new WorkflowModel();
        object.setCreationDate(new Date());
        object.setStatus(WORKFLOW_STATUS.EDITING);       
        object.save();
        redirect(request.controller + ".show", object._key());
    }
    
    public static void createNode(final Long id) {        
        WorkflowModel workflow = WorkflowModel.findById(id);
        WorkflowNodeModel object = new WorkflowNodeModel();
        object.setWorkflow(workflow);
        object.save();        
        renderJSON(object._key());
    }
    
    public static void deleteNode(final Long id) {        
        WorkflowNodeModel object = WorkflowNodeModel.findById(id);
        object.delete();
        ok();
    }
}