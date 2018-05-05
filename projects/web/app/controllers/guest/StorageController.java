package controllers.guest;

import controllers.CRUD.For;
import controllers.Check;
import controllers.adm.BaseAdminController;
import models.StorageModel;

@For(StorageModel.class)
@Check("/list/storages")
public class StorageController extends BaseAdminController {

}