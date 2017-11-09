package app.controllers.mocks;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import app.controllers.AbstractInstanceController;
import app.models.Body;
import app.models.InstanceModel;

@RestController
public class InstanceControllerMock extends AbstractInstanceController {

    @Override
    protected ResponseEntity<Body<List<InstanceModel>>> createInstance(
            String token, String identity, List<InstanceModel> listModel)
            throws Exception {
        return ResponseEntity.ok().body(
                Body.create(Body.OK, listModel));
    }
    @Override
    protected ResponseEntity<Body<InstanceModel>> getInstance(String token,
            String identity, String zone, String name) throws Exception {        
        InstanceModel model = new InstanceModel();
        model.setZone(zone);
        model.setName(name);
        return ResponseEntity.ok().body(
                Body.create(Body.OK, model));
    }
    @Override
    protected ResponseEntity<Body<Void>> deleteInstance(String token,
            String identity, String zone, String name) throws Exception {
        return ResponseEntity.ok().body(
                new Body<Void>(Body.OK)); 
    }
    @Override
    protected ResponseEntity<Body<List<InstanceModel>>> listInstances(
            String token, String identity) throws Exception {   
        List<InstanceModel> listRes = new ArrayList<>();
        listRes.add(new InstanceModel());
        listRes.add(new InstanceModel());        
        return ResponseEntity.ok().body(
                Body.create(Body.OK, listRes));
    }
 
}