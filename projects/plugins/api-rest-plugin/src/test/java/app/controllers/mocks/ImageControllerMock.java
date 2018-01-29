package app.controllers.mocks;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import app.controllers.AbstractImageController;
import app.models.Body;
import app.models.ImageModel;

@RestController
public class ImageControllerMock extends AbstractImageController {

    @Override
    protected ResponseEntity<Body<List<ImageModel>>> listImages(
            String token, String identity) throws Exception {   
        List<ImageModel> listRes = new ArrayList<>();
        listRes.add(new ImageModel());
        listRes.add(new ImageModel());        
        return ResponseEntity.ok().body(
                Body.create(Body.OK, listRes));
    }

    @Override
    protected ResponseEntity<Body<ImageModel>> getImage(
            String token, String identity, String name) throws Exception {        
        ImageModel res = new ImageModel();
        res.setName(name);
        return ResponseEntity.ok().body(
                Body.create(Body.OK, res));
    }
 
}