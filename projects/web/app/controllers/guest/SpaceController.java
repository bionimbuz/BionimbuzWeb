package controllers.guest;

import controllers.CRUD.For;
import controllers.Check;
import controllers.adm.BaseAdminController;
import models.SpaceModel;

@For(SpaceModel.class)
@Check("/list/spaces")
public class SpaceController extends BaseAdminController {

}