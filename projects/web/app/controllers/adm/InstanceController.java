package controllers.adm;

import controllers.CRUD.For;
import controllers.Check;
import models.InstanceModel;

@For(InstanceModel.class)
@Check("/adm/list/instances")
public class InstanceController extends BaseAdminController {
    
}