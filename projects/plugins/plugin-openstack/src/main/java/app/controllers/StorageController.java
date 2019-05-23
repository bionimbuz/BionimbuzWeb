package app.controllers;

import app.models.Body;
import app.models.PluginStorageFileDownloadModel;
import app.models.PluginStorageFileUploadModel;
import app.models.PluginStorageModel;
import org.openstack4j.api.OSClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import static app.common.OSClientHelper.getOSClient;

@RestController
public class StorageController extends AbstractStorageController {

    @Override
    protected ResponseEntity<Body<PluginStorageModel>> createSpace(String token,
            String identity, PluginStorageModel model) throws Exception {

        try {
            OSClient.OSClientV3 os = getOSClient(token);
            os.objectStorage().containers().create(model.getName());

            return ResponseEntity.ok(Body.create(model));
        } catch (final Exception e) {
            LOGGER.error(e.getMessage(), e);
        }

        return null;
    }

    @Override
    protected ResponseEntity<Body<Boolean>> deleteSpace(String token,
            String identity, String name) throws Exception {
        try {
            OSClient.OSClientV3 os = getOSClient(token);
            os.objectStorage().containers().delete(name);

            return new ResponseEntity<>(
                    Body.create(true),
                    HttpStatus.OK);
        } catch (final Exception e) {
            LOGGER.error(e.getMessage(), e);
        }

        return new ResponseEntity<>(
                Body.create(false),
                HttpStatus.NOT_FOUND);
    }

    @Override
    protected ResponseEntity<Body<PluginStorageFileUploadModel>> getUploadUrl(
            String name, String file) throws Exception {
        return null;
    }

    @Override
    protected ResponseEntity<Body<PluginStorageFileDownloadModel>> getDownloadUrl(String name,
            String file) throws Exception {
        return null;
    }
}
