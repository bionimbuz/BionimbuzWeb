package app.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import app.common.HttpHeadersCustom;
import app.common.Routes;
import app.models.Body;
import app.models.PriceTableModel;
import app.models.PriceTableStatusModel;

public abstract class AbstractPricingController extends BaseController{  
    protected static final Logger LOGGER = LoggerFactory.getLogger(AbstractPricingController.class);  

    /*
     * Action Methods
     */    
    @RequestMapping(path = Routes.PRICING, method = RequestMethod.GET)
    private ResponseEntity< Body< PriceTableModel >> getPricingAction(
            @RequestHeader(value=HttpHeadersCustom.API_VERSION) final String version) { 
        return callImplementedMethod("getPricing", version);        
    }    
    @RequestMapping(path = Routes.PRICING_STATUS, method = RequestMethod.GET)
    private ResponseEntity< Body< PriceTableStatusModel >> getPricingStatusAction(
            @RequestHeader(value=HttpHeadersCustom.API_VERSION) final String version) { 
        return callImplementedMethod("getPricingStatus", version);        
    }  
    
    /*
     * Abstract Methods
     */
    protected abstract ResponseEntity<Body<PriceTableModel>> 
                                getPricing() throws Exception;  
    protected abstract ResponseEntity<Body<PriceTableStatusModel>> 
                                getPricingStatus() throws Exception; 
}
