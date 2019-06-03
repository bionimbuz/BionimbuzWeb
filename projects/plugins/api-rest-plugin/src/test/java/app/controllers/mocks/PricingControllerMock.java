package app.controllers.mocks;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import app.controllers.AbstractPricingController;
import app.models.Body;
import app.models.PluginPriceModel;
import app.models.PluginPriceTableModel;
import app.models.PluginPriceTableStatusModel;

@RestController
public class PricingControllerMock extends AbstractPricingController {

    @Override
    protected ResponseEntity<Body<PluginPriceTableModel>> getPricing() throws Exception {
        PluginPriceTableModel model = new PluginPriceTableModel(
                PluginPriceTableStatusModel.createProcessingStatus(),
                new PluginPriceModel());
        return ResponseEntity.ok().body(
                Body.create(Body.OK, model));
    }

    @Override
    protected ResponseEntity<Body<PluginPriceTableModel>> getPricingWithToken(String token, String identity) throws Exception {
        PluginPriceTableModel model = new PluginPriceTableModel(
                PluginPriceTableStatusModel.createProcessingStatus(),
                new PluginPriceModel());
        return ResponseEntity.ok().body(
                Body.create(Body.OK, model));
    }

    @Override
    protected ResponseEntity<Body<PluginPriceTableStatusModel>> getPricingStatus()
            throws Exception {
        return ResponseEntity.ok().body(
                Body.create(Body.OK,PluginPriceTableStatusModel.createProcessingStatus()));
    }
}