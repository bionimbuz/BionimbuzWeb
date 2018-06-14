package app.controllers;

import javax.ws.rs.HttpMethod;

import org.jclouds.googlecloud.config.CurrentProject;
import org.jclouds.googlecloudstorage.GoogleCloudStorageApi;
import org.jclouds.googlecloudstorage.domain.Bucket;
import org.jclouds.googlecloudstorage.domain.DomainResourceReferences.Location;
import org.jclouds.googlecloudstorage.domain.DomainResourceReferences.StorageClass;
import org.jclouds.googlecloudstorage.domain.templates.BucketTemplate;
import org.jclouds.googlecloudstorage.features.BucketApi;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import app.common.GoogleCloudStorageUtils;
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

        try(GoogleCloudStorageApi googleApi =
                GoogleCloudStorageUtils.createApi(
                        identity,
                        token)) {
            Location location = Location.fromValue(model.getRegion().toUpperCase());
            String projectId =
                    CurrentProject.ClientEmail.toProjectNumber(identity);
            BucketApi bucketApi = googleApi.getBucketApi();
            BucketTemplate templategoogleApi =
                    new BucketTemplate().name(model.getName())
                        .location(location)
                        .storageClass(StorageClass.STANDARD);
            Bucket bucket = bucketApi.createBucket(projectId, templategoogleApi);
            if (bucket == null) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            return ResponseEntity.ok(
                    Body.create(model));
        }
    }

    @Override
    protected ResponseEntity<Body<Void>> deleteSpace(String token,
            String identity, String name) throws Exception {
        try(GoogleCloudStorageApi googleApi =
                GoogleCloudStorageUtils.createApi(
                        identity,
                        token)) {
            BucketApi bucketApi = googleApi.getBucketApi();
            Bucket bucket = bucketApi.getBucket(name);
            if(bucket == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            bucketApi.deleteBucket(name);
            return new ResponseEntity<>(HttpStatus.OK);
        }
    }

    @Override
    protected ResponseEntity<Body<PluginStorageFileUploadModel>> getUploadUrl(
            String name, String file) throws Exception {
        PluginStorageFileUploadModel model = new PluginStorageFileUploadModel();
        model.setFileName(file);
        model.setSpaceName(name);
        model.setMethod(HttpMethod.POST);
        model.setUrl(String.format(
                SystemConstants.STORAGE_FILE_UPLOAD_URL, name, file));
        return ResponseEntity.ok(
                Body.create(model));
    }

    @Override
    protected ResponseEntity<Body<PluginStorageFileDownloadModel>> getDownloadUrl(String name,
            String file) throws Exception {
        PluginStorageFileDownloadModel model = new PluginStorageFileDownloadModel();
        model.setFileName(file);
        model.setSpaceName(name);
        model.setMethod(HttpMethod.GET);
        model.setUrl(String.format(
                SystemConstants.STORAGE_FILE_DOWNLOAD_URL, name, file));
        return ResponseEntity.ok(
                Body.create(model));
    }
}
