package app.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import app.models.Body;
import app.models.PriceTableModel;
import app.models.PriceTableStatusModel;
import app.pricing.PriceTableScheduler;

@RestController
public class PricingController extends AbstractPricingController{  
    
    /*
     * Overwritten Methods
     */
    
    @Override
    protected ResponseEntity<Body<PriceTableModel>> getPricing() throws Exception {           
        return ResponseEntity.ok(
                Body.create(PriceTableScheduler.getPricing()));
    }    
    @Override
    protected ResponseEntity<Body<PriceTableStatusModel>> getPricingStatus() throws Exception {           
        return ResponseEntity.ok(
                Body.create(PriceTableScheduler.getPricingStatus()));
    }
}
