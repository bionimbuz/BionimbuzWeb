package app.controllers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.HttpMethod;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import app.common.ControllerRoutes;
import app.common.FileStorageService;
import app.common.FileUtils;
import app.common.SystemConstants;
import app.models.Body;
import app.models.PluginStorageFileDownloadModel;
import app.models.PluginStorageFileUploadModel;
import app.models.PluginStorageModel;

@RestController
public class StorageController extends AbstractStorageController {

    @Override
    protected ResponseEntity<Body<PluginStorageModel>> createSpace(String token,
            String identity, PluginStorageModel model) throws Exception {
        File dir = new File(getSpacePath(model.getName()));
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return ResponseEntity.ok(
                Body.create(model));
    }

    @Override
    protected ResponseEntity<Body<Boolean>> deleteSpace(String token,
            String identity, String name) throws Exception {
        File spaceDir = new File(getSpacePath(name));
        if (!spaceDir.exists()) {
            return new ResponseEntity<>(
                    Body.create(false),
                    HttpStatus.OK);
        }
        FileUtils.deleteDir(spaceDir);
        return new ResponseEntity<>(
                Body.create(true),
                HttpStatus.OK);
    }

    @Override
    protected ResponseEntity<Body<PluginStorageFileUploadModel>> getUploadUrl(
            String name, String file) throws Exception {
        PluginStorageFileUploadModel model = new PluginStorageFileUploadModel();
        model.setFileName(file);
        model.setSpaceName(name);
        model.setMethod(HttpMethod.POST);
        model.setUrl(createUploadUrl(name, file));
        return ResponseEntity.ok(
                Body.create(model));
    }

    @Override
    protected ResponseEntity<Body<PluginStorageFileDownloadModel>> getDownloadUrl(String name,
            String file) throws Exception {
        PluginStorageFileDownloadModel model =
                new PluginStorageFileDownloadModel();
        model.setSpaceName(name);
        model.setFileName(file);
        model.setMethod(HttpMethod.GET);
        model.setUrl(createDownloadUrl(name, file));
        return ResponseEntity.ok(
                Body.create(model));
    }

    private String createUploadUrl(final String space, final String fileName) {
        return ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/spaces/")
                .path(space)
                .path("/file/")
                .path(fileName)
                .path("/upload")
                .toUriString();
    }

    private String createDownloadUrl(final String space, final String fileName) {
        return ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/spaces/")
                .path(space)
                .path("/file/")
                .path(fileName)
                .path("/download")
                .toUriString();
    }

    @CrossOrigin
    @PostMapping(ControllerRoutes.SPACES_NAME_FILE_UPLOAD)
    public ResponseEntity<Boolean> uploadFile(
            @PathVariable String name,
            @PathVariable String file_name,
            @RequestBody byte[] body) {
        try {
            File file = new File(getSpacePath(name), file_name);
            try (FileOutputStream fos = new FileOutputStream(file)) {
                fos.write(body);
            }
            return ResponseEntity.ok(true);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.ok(false);
        }
    }

    @CrossOrigin
    @GetMapping(ControllerRoutes.SPACES_NAME_FILE_DOWNLOAD)
    public ResponseEntity<Resource> downloadFile(
            @PathVariable String name,
            @PathVariable String file_name,
            HttpServletRequest request) {
        try {
            FileStorageService storage;
            storage = new FileStorageService(getSpacePath(name));
            // Load file as Resource
            Resource resource = storage.loadFileAsResource(file_name);

            // Try to determine file's content type
            String contentType = request.getServletContext()
                    .getMimeType(resource.getFile().getAbsolutePath());

            // Fallback to the default content type if type could not be determined
            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    private static String getSpacePath(final String space) {
        return SystemConstants.SPACES_DIR + space;
    }
}
