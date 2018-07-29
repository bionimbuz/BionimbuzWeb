package app.controllers.mocks;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import app.controllers.AbstractComputingController;
import app.models.Body;
import app.models.PluginComputingInstanceModel;
import app.models.PluginComputingRegionModel;
import app.models.PluginComputingZoneModel;

@RestController
public class ComputingControllerMock extends AbstractComputingController {

    @Override
    protected ResponseEntity<Body<List<PluginComputingInstanceModel>>> createInstances(
            String token, String identity, List<PluginComputingInstanceModel> listModel)
            throws Exception {
        return ResponseEntity.ok().body(
                Body.create(Body.OK, listModel));
    }
    @Override
    protected ResponseEntity<Body<PluginComputingInstanceModel>> getInstance(String token,
            String identity, String region, String zone, String name) throws Exception {        
        PluginComputingInstanceModel model = new PluginComputingInstanceModel();
        model.setZone(zone);
        model.setName(name);
        return ResponseEntity.ok().body(
                Body.create(Body.OK, model));
    }
    @Override
    protected ResponseEntity<Body<Boolean>> deleteInstance(String token,
            String identity, String region, String zone, String name) throws Exception {
        return ResponseEntity.ok().body(
                new Body<Boolean>(Body.OK)); 
    }
    @Override
    protected ResponseEntity<Body<List<PluginComputingInstanceModel>>> listInstances(
            String token, String identity) throws Exception {   
        List<PluginComputingInstanceModel> listRes = new ArrayList<>();
        listRes.add(new PluginComputingInstanceModel());
        listRes.add(new PluginComputingInstanceModel());        
        return ResponseEntity.ok().body(
                Body.create(Body.OK, listRes));
    }    
    @Override
    protected ResponseEntity<Body<List<PluginComputingRegionModel>>> listRegions(
            String token, String identity) throws Exception {
        List<PluginComputingRegionModel> listRes = new ArrayList<>();
        listRes.add(new PluginComputingRegionModel("region1"));
        listRes.add(new PluginComputingRegionModel("region2"));        
        return ResponseEntity.ok().body(
                Body.create(Body.OK, listRes));
    }
    @Override
    protected ResponseEntity<Body<List<PluginComputingZoneModel>>> listRegionZones(
            String token, String identity, String name) throws Exception {
        List<PluginComputingZoneModel> listRes = new ArrayList<>();
        listRes.add(new PluginComputingZoneModel("zone1"));
        listRes.add(new PluginComputingZoneModel("zone2"));        
        return ResponseEntity.ok().body(
                Body.create(Body.OK, listRes));
    } 
}