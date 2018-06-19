package app.controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import app.models.Body;
import app.models.PluginImageModel;

@RestController
public class ImageController extends AbstractImageController{

    /*
     * Overwritten Methods
     */

    @Override
    protected ResponseEntity<Body<PluginImageModel>> getImage(
            final String token, final String identity, final String name) throws Exception {
        return ResponseEntity.ok(
                Body.create(createImageModel()));
    }

    @Override
    protected ResponseEntity<Body<List<PluginImageModel>>> listImages(
            final String token,
            final String identity) throws Exception  {
        List<PluginImageModel> res = new ArrayList<>();
        res.add(createImageModel());
        return ResponseEntity.ok(
                Body.create(res));
    }

    private String getLocalSOVersion() {
        String soVersion = System.getProperty("os.name");
        soVersion += "-" + System.getProperty("os.version");
        soVersion += "-" + System.getProperty("os.arch");
        return soVersion.toLowerCase();
    }

    private PluginImageModel createImageModel() {
        return new PluginImageModel(
                "",
                getLocalSOVersion(),
                "");
    }
}
