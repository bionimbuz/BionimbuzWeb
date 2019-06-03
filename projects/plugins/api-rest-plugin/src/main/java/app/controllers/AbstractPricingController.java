package app.controllers;

import com.google.common.net.HttpHeaders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import app.common.HttpHeadersCustom;
import app.common.Routes;
import app.models.Body;
import app.models.PluginPriceTableModel;
import app.models.PluginPriceTableStatusModel;

public abstract class AbstractPricingController extends BaseControllerVersioned{
    protected static final Logger LOGGER = LoggerFactory.getLogger(AbstractPricingController.class);

    /*
     * Action Methods
     */
    @RequestMapping(path = Routes.PRICING, method = RequestMethod.GET)
    private ResponseEntity< Body< PluginPriceTableModel >> getPricingAction(
            @RequestHeader(value=HttpHeadersCustom.API_VERSION) final String version) {
        return callImplementedMethod("getPricing", version);
    }
    @RequestMapping(path = Routes.PRICING_TOKEN, method = RequestMethod.GET)
    private ResponseEntity< Body< PluginPriceTableModel >> getPricingWithTokenAction(
            @RequestHeader(value=HttpHeadersCustom.API_VERSION) final String version,
            @RequestHeader(value= HttpHeaders.AUTHORIZATION) final String token,
            @RequestHeader(value=HttpHeadersCustom.AUTHORIZATION_ID) final String identity) {
        return callImplementedMethod("getPricingWithToken", version, token, identity);
    }
    @RequestMapping(path = Routes.PRICING_STATUS, method = RequestMethod.GET)
    private ResponseEntity< Body< PluginPriceTableStatusModel >> getPricingStatusAction(
            @RequestHeader(value=HttpHeadersCustom.API_VERSION) final String version) {
        return callImplementedMethod("getPricingStatus", version);
    }

    /*
     * Abstract Methods
     */
    protected abstract ResponseEntity<Body<PluginPriceTableModel>>
                                getPricing() throws Exception;

    protected abstract ResponseEntity<Body<PluginPriceTableModel>>
                    getPricingWithToken(final String token, final String identity) throws Exception;


    protected abstract ResponseEntity<Body<PluginPriceTableStatusModel>>
                                getPricingStatus() throws Exception;
}
