package app.controllers.mocks;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import app.controllers.AbstractPricingController;
import app.models.Body;
import app.models.PriceModel;
import app.models.PriceTableModel;
import app.models.PriceTableStatusModel;

@RestController
public class PricingControllerMock extends AbstractPricingController {

    @Override
    protected ResponseEntity<Body<PriceTableModel>> getPricing() throws Exception {
        PriceTableModel model = new PriceTableModel(
                PriceTableStatusModel.createProcessingStatus(),
                new PriceModel());
        return ResponseEntity.ok().body(
                Body.create(Body.OK, model));
    }
    @Override
    protected ResponseEntity<Body<PriceTableStatusModel>> getPricingStatus()
            throws Exception {
        return ResponseEntity.ok().body(
                Body.create(Body.OK,PriceTableStatusModel.createProcessingStatus()));
    }
}