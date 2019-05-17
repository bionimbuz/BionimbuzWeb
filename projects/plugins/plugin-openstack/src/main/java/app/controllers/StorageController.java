package app.controllers;

import app.models.Body;
import app.models.PluginStorageFileDownloadModel;
import app.models.PluginStorageFileUploadModel;
import app.models.PluginStorageModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StorageController extends AbstractStorageController {

    @Override
    protected ResponseEntity<Body<PluginStorageModel>> createSpace(String token,
            String identity, PluginStorageModel model) throws Exception {
        return null;
    }

    @Override
    protected ResponseEntity<Body<Boolean>> deleteSpace(String token,
            String identity, String name) throws Exception {
        return null;
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
