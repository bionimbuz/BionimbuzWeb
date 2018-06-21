package app.controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import app.common.SystemConstants;
import app.models.Body;
import app.models.PluginInstanceRegionModel;
import app.models.PluginInstanceZoneModel;

@RestController
public class InstanceRegionController extends AbstractInstanceRegionController{

    /*
     * Overwritten Methods
     */
    @Override
    protected ResponseEntity<Body<List<PluginInstanceRegionModel>>> listInstanceRegions(
            final String token,
            final String identity) throws Exception {

        List<PluginInstanceRegionModel> res = new ArrayList<>();
        res.add(createRegionModel());
        return ResponseEntity.ok(
                Body.create(res));
    }
    
    @Override
    protected ResponseEntity<Body<List<PluginInstanceZoneModel>>> listInstanceRegionsZones(
            final String token,
            final String identity,
            final String name) throws Exception {        
        if(SystemConstants.PLUGIN_REGION.equals(name)) {
            List<PluginInstanceZoneModel> res = new ArrayList<>();
            res.add(InstanceZoneController.createZoneModel());
            return ResponseEntity.ok(
                    Body.create(res));
        }
        return new ResponseEntity<>(
                HttpStatus.NOT_FOUND);
    }

    private PluginInstanceRegionModel createRegionModel() {
        return new PluginInstanceRegionModel(
                        SystemConstants.PLUGIN_REGION);
    }
}
