package controllers.adm;

import controllers.CRUD.For;
import controllers.Check;
import models.SettingModel;

@For(SettingModel.class)
@Check("/adm/list/settings")
public class SettingController extends BaseAdminController {
}