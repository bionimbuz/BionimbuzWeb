package app.controllers.mocks;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import app.controllers.AbstractInstanceController;
import app.models.Body;
import app.models.PluginInstanceModel;

@RestController
public class InstanceControllerMock extends AbstractInstanceController {

    @Override
    protected ResponseEntity<Body<List<PluginInstanceModel>>> createInstance(
            String token, String identity, List<PluginInstanceModel> listModel)
            throws Exception {
        return ResponseEntity.ok().body(
                Body.create(Body.OK, listModel));
    }
    @Override
    protected ResponseEntity<Body<PluginInstanceModel>> getInstance(String token,
            String identity, String zone, String name) throws Exception {        
        PluginInstanceModel model = new PluginInstanceModel();
        model.setZone(zone);
        model.setName(name);
        return ResponseEntity.ok().body(
                Body.create(Body.OK, model));
    }
    @Override
    protected ResponseEntity<Body<Boolean>> deleteInstance(String token,
            String identity, String zone, String name) throws Exception {
        return ResponseEntity.ok().body(
                new Body<Boolean>(Body.OK)); 
    }
    @Override
    protected ResponseEntity<Body<List<PluginInstanceModel>>> listInstances(
            String token, String identity) throws Exception {   
        List<PluginInstanceModel> listRes = new ArrayList<>();
        listRes.add(new PluginInstanceModel());
        listRes.add(new PluginInstanceModel());        
        return ResponseEntity.ok().body(
                Body.create(Body.OK, listRes));
    }
 
}