package app.controllers.mocks;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import app.controllers.AbstractPricingController;
import app.models.Body;
import app.models.PricingModel;
import app.models.PricingStatusModel;

@RestController
public class PricingControllerMock extends AbstractPricingController {

    @Override
    protected ResponseEntity<Body<PricingModel>> getPricing() throws Exception {
        PricingModel model = new PricingModel(
                PricingStatusModel.createProcessingStatus());
        return ResponseEntity.ok().body(
                Body.create(Body.OK, model));
    }
    @Override
    protected ResponseEntity<Body<PricingStatusModel>> getPricingStatus()
            throws Exception {
        return ResponseEntity.ok().body(
                Body.create(Body.OK,PricingStatusModel.createProcessingStatus()));
    }
}