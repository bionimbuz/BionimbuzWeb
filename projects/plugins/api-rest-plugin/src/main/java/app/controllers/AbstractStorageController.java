package app.controllers;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.google.common.net.HttpHeaders;

import app.common.HttpHeadersCustom;
import app.common.Routes;
import app.models.Body;
import app.models.PluginInstanceModel;
import app.models.PluginStorageFileDownloadModel;
import app.models.PluginStorageFileUploadModel;
import app.models.PluginStorageModel;

public abstract class AbstractStorageController extends BaseController {
    protected static final Logger LOGGER = LoggerFactory.getLogger(AbstractStorageController.class);

    /*
     * Action Methods
     */

    @RequestMapping(path = Routes.SPACES, method = RequestMethod.POST)
    private ResponseEntity<Body<List<PluginInstanceModel>>> createSpaceAction(
            @RequestHeader(value=HttpHeadersCustom.API_VERSION) final String version,
            @RequestHeader(value=HttpHeaders.AUTHORIZATION) final String token,
            @RequestHeader(value=HttpHeadersCustom.AUTHORIZATION_ID) final String identity,
            @RequestBody PluginStorageModel model) {
        return callImplementedMethod("createSpace", version, token, identity, model);
    }
    @RequestMapping(path = Routes.SPACES_NAME, method = RequestMethod.DELETE)
    private ResponseEntity<Body<Void>> deleteSpaceAction(
            @RequestHeader(value=HttpHeadersCustom.API_VERSION) final String version,
            @RequestHeader(value=HttpHeaders.AUTHORIZATION) final String token,
            @RequestHeader(value=HttpHeadersCustom.AUTHORIZATION_ID) final String identity,
            @PathVariable(value = "name") final String name) {
        return callImplementedMethod("deleteSpace", version, token, identity, name);
    }
    @RequestMapping(path = Routes.SPACES_NAME_UPLOAD_FILE, method = RequestMethod.GET)
    private ResponseEntity<Body<PluginStorageFileUploadModel>> getUploadUrlAction(
            @RequestHeader(value=HttpHeadersCustom.API_VERSION) final String version,
            @PathVariable(value = "name") final String name,
            @PathVariable(value = "file") final String file) {
        return callImplementedMethod("getUploadUrl", version, name, file);
    }
    @RequestMapping(path = Routes.SPACES_NAME_DOWNLOAD_FILE, method = RequestMethod.GET)
    private ResponseEntity<Body<PluginStorageFileDownloadModel>> getDownloadUrlAction(
            @RequestHeader(value=HttpHeadersCustom.API_VERSION) final String version,
            @PathVariable(value = "name") final String name,
            @PathVariable(value = "file") final String file) {
        return callImplementedMethod("getDownloadUrl", version, name, file);
    }

    /*
     * Abstract Methods
     */

    protected abstract ResponseEntity<Body<PluginStorageModel>> createSpace(
            final String token,
            final String identity,
            PluginStorageModel model) throws Exception;
    protected abstract ResponseEntity<Body<Void>> deleteSpace(
            final String token,
            final String identity,
            final String name) throws Exception;
    protected abstract ResponseEntity<Body<PluginStorageFileUploadModel>> getUploadUrl(
            final String name,
            final String file) throws Exception;
    protected abstract ResponseEntity<Body<PluginStorageFileDownloadModel>> getDownloadUrl(
            final String name,
            final String file) throws Exception;
}
