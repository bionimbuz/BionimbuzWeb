package app.controllers;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.google.common.net.HttpHeaders;

import app.common.HttpHeadersCustom;
import app.common.Routes;
import app.models.Body;
import app.models.PluginInstanceRegionModel;
import app.models.PluginInstanceZoneModel;

public abstract class AbstractInstanceRegionController extends BaseController {

    protected static final Logger LOGGER = LoggerFactory.getLogger(AbstractInstanceRegionController.class);

    /*
     * Action Methods
     */
    @RequestMapping(path = Routes.INSTANCE_REGIONS, method = RequestMethod.GET)
    private ResponseEntity<Body<List<PluginInstanceRegionModel>>> listInstanceRegionsAction(
            @RequestHeader(value = HttpHeadersCustom.API_VERSION) final String version,
            @RequestHeader(value = HttpHeaders.AUTHORIZATION) final String token,
            @RequestHeader(value = HttpHeadersCustom.AUTHORIZATION_ID) final String identity) {
        return this.callImplementedMethod("listInstanceRegions", version, token, identity);
    }

    @RequestMapping(path = Routes.INSTANCE_REGIONS_ZONES, method = RequestMethod.GET)
    private ResponseEntity<Body<List<PluginInstanceZoneModel>>> listInstanceRegionsZonesAction(
            @RequestHeader(value = HttpHeadersCustom.API_VERSION) final String version,
            @RequestHeader(value = HttpHeaders.AUTHORIZATION) final String token,
            @RequestHeader(value = HttpHeadersCustom.AUTHORIZATION_ID) final String identity,
            @PathVariable(value = "name") final String name) {
        return this.callImplementedMethod("listInstanceRegionsZones", version, token, identity, name);
    }

    /*
     * Abstract Methods
     */
    protected abstract ResponseEntity<Body<List<PluginInstanceRegionModel>>> listInstanceRegions(
            final String token,
            final String identity) throws Exception;

    protected abstract ResponseEntity<Body<List<PluginInstanceZoneModel>>> listInstanceRegionsZones(
            final String token,
            final String identity,
            final String name) throws Exception;

}
