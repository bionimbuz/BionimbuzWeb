package controllers.guest;

import java.util.Date;
import java.util.List;

import app.common.utils.StringUtils;
import controllers.CRUD.For;
import controllers.Check;
import controllers.adm.BaseAdminController;
import jobs.WorkflowExecutionJob;
import models.WorkflowModel;
import models.WorkflowModel.WORKFLOW_STATUS;
import models.WorkflowNodeModel;
import play.data.binding.Binder;
import play.data.validation.Validation;
import play.db.Model;
import play.exceptions.TemplateNotFoundException;
import play.i18n.Messages;

@For(WorkflowModel.class)
@Check("/list/workflows")
public class WorkflowController extends BaseAdminController {
    
    public static void list(int page, String search, String searchFields, String orderBy, String order) {
        final CustomObjectType type = CustomObjectType.get(getControllerClass());
        notFoundIfNull(type);
        if (page < 1) {
            page = 1;
        }        
        if(StringUtils.isEmpty(orderBy)) {
            orderBy = "creationDate";
            order = "DESC";
        }
        final List<Model> objects = type.findPage(page, search, searchFields, orderBy, order, (String) request.args.get("where"));
        final Long count = type.count(search, searchFields, (String) request.args.get("where"));
        final Long totalCount = type.count(null, null, (String) request.args.get("where"));
        try {
            render(type, objects, count, totalCount, page, orderBy, order);
        } catch (final TemplateNotFoundException e) {
            render("CRUD/list.html", type, objects, count, totalCount, page, orderBy, order);
        }
    }
    
    public static void save(final Long id) throws Exception {
        final CustomObjectType type = CustomObjectType.get(getControllerClass());
        notFoundIfNull(type);
        final WorkflowModel object = WorkflowModel.findById(id);
        notFoundIfNull(object);
        WORKFLOW_STATUS previousStatus = object.getStatus();
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
        if(previousStatus == WORKFLOW_STATUS.EDITING 
                && object.getStatus() == WORKFLOW_STATUS.RUNNING) {
            (new WorkflowExecutionJob(object.getId())).doJob();            
        }
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
        object.setUser(getConnectedUser());
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

    public static void saveModel(
            final Long id, 
            final String jsonModel,
            final String jsonGraph) {
        WorkflowModel workflow = WorkflowModel.findById(id);
        workflow.setJsonGraph(jsonGraph);
        workflow.setJsonModel(jsonModel);
        workflow.save();
        ok();    
    }
}