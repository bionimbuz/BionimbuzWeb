package app.controllers;

import java.util.List;

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
import app.models.PluginInstanceZoneModel;

public abstract class AbstractInstanceZoneController extends BaseController {
    protected static final Logger LOGGER = LoggerFactory.getLogger(AbstractInstanceZoneController.class);  

    /*
     * Action Methods
     */
    @RequestMapping(path = Routes.INSTANCE_ZONES, method = RequestMethod.GET)
    private ResponseEntity<Body<List<PluginInstanceZoneModel>>> listInstancesAction(
            @RequestHeader(value=HttpHeadersCustom.API_VERSION) final String version,
            @RequestHeader(value=HttpHeaders.AUTHORIZATION) final String token, 
            @RequestHeader(value=HttpHeadersCustom.AUTHORIZATION_ID) final String identity) {
        return callImplementedMethod("listZones", version, token, identity);        
    }

    /*
     * Abstract Methods
     */
    protected abstract ResponseEntity<Body<List<PluginInstanceZoneModel>>> listZones(
            final String token, 
            final String identity) throws Exception;

}
