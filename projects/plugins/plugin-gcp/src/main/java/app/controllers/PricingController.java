package app.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import app.models.Body;
import app.models.PricingModel;
import app.models.PricingStatusModel;
import app.pricing.PriceTableScheduler;

@RestController
public class PricingController extends AbstractPricingController{  
    
    /*
     * Overwritten Methods
     */
    
    @Override
    protected ResponseEntity<Body<PricingModel>> getPricing() throws Exception {           
        return ResponseEntity.ok(
                Body.create(PriceTableScheduler.getPricing()));
    }    
    @Override
    protected ResponseEntity<Body<PricingStatusModel>> getPricingStatus() throws Exception {           
        return ResponseEntity.ok(
                Body.create(PriceTableScheduler.getPricingStatus()));
    }
}