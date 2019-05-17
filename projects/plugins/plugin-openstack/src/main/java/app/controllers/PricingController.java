package app.controllers;

import app.common.PriceTableSingleton;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import app.models.Body;
import app.models.PluginPriceTableModel;
import app.models.PluginPriceTableStatusModel;

@RestController
public class PricingController extends AbstractPricingController {

    @Autowired
    private PriceTableSingleton priceTable;

    
    /*
     * Overwritten Methods
     */

    @Override
    protected ResponseEntity<Body<PluginPriceTableModel>> getPricing() throws Exception {
        PluginPriceTableModel res = new PluginPriceTableModel(priceTable.getStatus(), priceTable.getPrice());
        return ResponseEntity.ok(Body.create(res));
    }

    @Override
    protected ResponseEntity<Body<PluginPriceTableStatusModel>> getPricingStatus() throws Exception {
        return ResponseEntity.ok(Body.create(priceTable.getStatus()));
    }
}
