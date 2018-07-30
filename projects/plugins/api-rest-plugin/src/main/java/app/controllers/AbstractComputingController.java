package app.controllers;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.google.common.net.HttpHeaders;

import app.common.HttpHeadersCustom;
import app.common.Routes;
import app.models.Body;
import app.models.PluginComputingInstanceModel;
import app.models.PluginComputingRegionModel;
import app.models.PluginComputingZoneModel;

public abstract class AbstractComputingController extends BaseControllerVersioned {
    protected static final Logger LOGGER = LoggerFactory.getLogger(AbstractComputingController.class);

    /*
     * Action Methods
     */

    @RequestMapping(path = Routes.COMPUTING_INSTANCES, method = RequestMethod.POST)
    private ResponseEntity<Body<List<PluginComputingInstanceModel>>> createInstancesAction(
            @RequestHeader(value=HttpHeadersCustom.API_VERSION) final String version,
            @RequestHeader(value=HttpHeaders.AUTHORIZATION) final String token,
            @RequestHeader(value=HttpHeadersCustom.AUTHORIZATION_ID) final String identity,
            @RequestBody List<PluginComputingInstanceModel> listModel) {
        return callImplementedMethod("createInstances", version, token, identity, listModel);
    }
    @RequestMapping(path = Routes.COMPUTING_REGIONS_ZONES_INSTANCES_NAME_, method = RequestMethod.GET)
    private ResponseEntity<Body<PluginComputingInstanceModel>> getInstanceAction(
            @RequestHeader(value=HttpHeadersCustom.API_VERSION) final String version,
            @RequestHeader(value=HttpHeaders.AUTHORIZATION) final String token,
            @RequestHeader(value=HttpHeadersCustom.AUTHORIZATION_ID) final String identity,
            @PathVariable(value = "region") final String region,
            @PathVariable(value = "zone") final String zone,
            @PathVariable(value = "name") final String name) {
        return callImplementedMethod("getInstance", version, token, identity, region, zone, name);
    }
    @RequestMapping(path = Routes.COMPUTING_REGIONS_ZONES_INSTANCES_NAME_, method = RequestMethod.DELETE)
    private ResponseEntity<Body<Boolean>> deleteInstanceAction(
            @RequestHeader(value=HttpHeadersCustom.API_VERSION) final String version,
            @RequestHeader(value=HttpHeaders.AUTHORIZATION) final String token,
            @RequestHeader(value=HttpHeadersCustom.AUTHORIZATION_ID) final String identity,
            @PathVariable(value = "region") final String region,
            @PathVariable(value = "zone") final String zone,
            @PathVariable(value = "name") final String name) {
        return callImplementedMethod("deleteInstance", version, token, identity, region, zone, name);
    }
    @RequestMapping(path = Routes.COMPUTING_INSTANCES, method = RequestMethod.GET)
    private ResponseEntity<Body<List<PluginComputingInstanceModel>>> listInstancesAction(
            @RequestHeader(value=HttpHeadersCustom.API_VERSION) final String version,
            @RequestHeader(value=HttpHeaders.AUTHORIZATION) final String token,
            @RequestHeader(value=HttpHeadersCustom.AUTHORIZATION_ID) final String identity) {
        return callImplementedMethod("listInstances", version, token, identity);
    }    
    @RequestMapping(path = Routes.COMPUTING_REGIONS, method = RequestMethod.GET)
    private ResponseEntity<Body<List<PluginComputingRegionModel>>> listRegionsAction(
            @RequestHeader(value = HttpHeadersCustom.API_VERSION) final String version,
            @RequestHeader(value = HttpHeaders.AUTHORIZATION) final String token,
            @RequestHeader(value = HttpHeadersCustom.AUTHORIZATION_ID) final String identity) {
        return this.callImplementedMethod("listRegions", version, token, identity);
    }
    @RequestMapping(path = Routes.COMPUTING_REGIONS_ZONES_, method = RequestMethod.GET)
    private ResponseEntity<Body<List<PluginComputingZoneModel>>> listRegionZonesAction(
            @RequestHeader(value = HttpHeadersCustom.API_VERSION) final String version,
            @RequestHeader(value = HttpHeaders.AUTHORIZATION) final String token,
            @RequestHeader(value = HttpHeadersCustom.AUTHORIZATION_ID) final String identity,
            @PathVariable(value = "name") final String name) {
        return this.callImplementedMethod("listRegionZones", version, token, identity, name);
    }

    /*
     * Abstract Methods
     */

    protected abstract ResponseEntity<Body<List<PluginComputingInstanceModel>>> createInstances(
            final String token,
            final String identity,
            List<PluginComputingInstanceModel> listModel) throws Exception;
    protected abstract ResponseEntity<Body<PluginComputingInstanceModel>> getInstance(
            final String token,
            final String identity,
            final String region,
            final String zone,
            final String name) throws Exception;
    protected abstract ResponseEntity<Body<Boolean>> deleteInstance(
            final String token,
            final String identity,
            final String region,
            final String zone,
            final String name) throws Exception;
    protected abstract ResponseEntity<Body<List<PluginComputingInstanceModel>>> listInstances(
            final String token,
            final String identity) throws Exception;
    protected abstract ResponseEntity<Body<List<PluginComputingRegionModel>>> listRegions(
            final String token,
            final String identity) throws Exception;
    protected abstract ResponseEntity<Body<List<PluginComputingZoneModel>>> listRegionZones(
            final String token,
            final String identity,
            final String name) throws Exception;

}
