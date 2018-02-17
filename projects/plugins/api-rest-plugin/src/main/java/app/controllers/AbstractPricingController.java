package app.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.google.common.net.HttpHeaders;

import app.common.HttpHeadersCustom;
import app.common.Routes;
import app.models.Body;
import app.models.PricingModel;

public abstract class AbstractPricingController extends BaseController{  
    protected static final Logger LOGGER = LoggerFactory.getLogger(AbstractPricingController.class);  

    /*
     * Action Methods
     */    
    @RequestMapping(path = Routes.PRICING, method = RequestMethod.GET)
    private ResponseEntity< Body< PricingModel >> getPricingAction(
            @RequestHeader(value=HttpHeadersCustom.API_VERSION) final String version,
            @RequestHeader(value=HttpHeaders.AUTHORIZATION) final String token, 
            @RequestHeader(value=HttpHeadersCustom.AUTHORIZATION_ID) final String identity) { 
        return callImplementedMethod("getPricing", version, token, identity);        
    }  
    
    /*
     * Abstract Methods
     */
    protected abstract ResponseEntity<Body<PricingModel>> getPricing(
            final String token, 
            final String identity) throws Exception;  
}
