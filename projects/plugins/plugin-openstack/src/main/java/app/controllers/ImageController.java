package app.controllers;

import app.models.Body;
import app.models.PluginImageModel;
import org.openstack4j.api.OSClient;
import org.openstack4j.model.compute.Image;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static app.common.OSClientHelper.getOSClient;
import static app.common.OSClientHelper.retrieveCredentialData;

@RestController
public class ImageController extends AbstractImageController{

    /*
     * Overwritten Methods
     */

    @Override
    protected ResponseEntity<Body<PluginImageModel>> getImage(
            final String token, final String identity, final String imageId) throws Exception {

        try {

            Map<String,String> clientData = retrieveCredentialData(identity);

            OSClient.OSClientV3 os = getOSClient(token, clientData.get("host"), clientData.get("project_id"));

            Image image = os.compute().images().get(imageId);
            PluginImageModel res = new PluginImageModel(image.getId(), image.getName(), image.getLinks().get(0).getHref());

            return ResponseEntity.ok(Body.create(res));
        } catch (final Exception e) {
            LOGGER.error(e.getMessage(), e);
        }

        return null;
    }
    
    @Override
    protected ResponseEntity<Body<List<PluginImageModel>>> listImages(
            final String token,
            final String identity) throws Exception  {
        try {

            Map<String,String> clientData = retrieveCredentialData(identity);

            OSClient.OSClientV3 os = getOSClient(token, clientData.get("host"), clientData.get("project_id"));

            List<? extends Image> images = os.compute().images().list();
            List<PluginImageModel> res = imagesToModel(images);

            return ResponseEntity.ok(Body.create(res));
        } catch (final Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return null;
    }


    private List<PluginImageModel> imagesToModel(List<? extends Image> images) {
        List<PluginImageModel> models = new ArrayList<>();
        for(Image image : images) {
            PluginImageModel model = new PluginImageModel();
            model.setId(image.getId());
            model.setName(image.getName());
            model.setUrl(image.getLinks().get(0).getHref());
            models.add(model);
        }
        return models;
    }

}
