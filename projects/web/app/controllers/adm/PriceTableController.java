package controllers.adm;

import controllers.CRUD.For;
import controllers.Check;
import models.PriceTableModel;

@For(PriceTableModel.class)
@Check("/adm/list/prices")
public class PriceTableController extends BaseAdminController{

    public static void forceSync() {
        ok();
    }
}
