package app.controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import app.common.SystemConstants;
import app.models.Body;
import app.models.PluginInstanceZoneModel;

@RestController
public class InstanceZoneController extends AbstractInstanceZoneController{

    /*
     * Overwritten Methods
     */
    @Override
    protected ResponseEntity<Body<List<PluginInstanceZoneModel>>> listZones(
            final String token,
            final String identity) throws Exception {

        List<PluginInstanceZoneModel> res = new ArrayList<>();
        res.add(createZoneModel());
        return ResponseEntity.ok(
                Body.create(res));
    }

    public static PluginInstanceZoneModel createZoneModel() {
        return new PluginInstanceZoneModel(
                        SystemConstants.PLUGIN_ZONE);
    }
}
