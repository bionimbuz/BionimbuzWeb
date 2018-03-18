package app.controllers.mocks;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import app.controllers.AbstractImageController;
import app.models.Body;
import app.models.PluginImageModel;

@RestController
public class ImageControllerMock extends AbstractImageController {

    @Override
    protected ResponseEntity<Body<List<PluginImageModel>>> listImages(
            String token, String identity) throws Exception {   
        List<PluginImageModel> listRes = new ArrayList<>();
        listRes.add(new PluginImageModel());
        listRes.add(new PluginImageModel());        
        return ResponseEntity.ok().body(
                Body.create(Body.OK, listRes));
    }

    @Override
    protected ResponseEntity<Body<PluginImageModel>> getImage(
            String token, String identity, String name) throws Exception {        
        PluginImageModel res = new PluginImageModel();
        res.setName(name);
        return ResponseEntity.ok().body(
                Body.create(Body.OK, res));
    }
 
}