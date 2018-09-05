package controllers.guest;

import controllers.CRUD.For;
import controllers.Check;
import controllers.adm.BaseAdminController;
import models.WorkflowModel;

@For(WorkflowModel.class)
@Check("/list/workflows")
public class WorkflowController extends BaseAdminController {
}