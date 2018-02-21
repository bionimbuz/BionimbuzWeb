package controllers.adm;

import controllers.CRUD.For;
import controllers.Check;
import jobs.PriceTableUpdaterJob;
import models.PriceTableModel;

@For(PriceTableModel.class)
@Check("/adm/list/prices")
public class PriceTableController extends BaseAdminController{
    public static void forceSync() {
        (new PriceTableUpdaterJob()).now();
        ok();
    }
}
