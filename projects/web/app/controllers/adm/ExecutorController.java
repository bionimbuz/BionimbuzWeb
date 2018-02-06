package controllers.adm;

import controllers.CRUD.For;
import controllers.Check;
import models.ExecutorModel;

@For(ExecutorModel.class)
@Check("/adm/list/executors")
public class ExecutorController extends BaseAdminController {
    
}