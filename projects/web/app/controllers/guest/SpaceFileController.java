package controllers.guest;

import controllers.CRUD.For;
import controllers.Check;
import controllers.adm.BaseAdminController;
import models.SpaceFileModel;

@For(SpaceFileModel.class)
@Check("/list/space/files")
public class SpaceFileController extends BaseAdminController {

}
