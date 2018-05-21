package app.controllers;

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
import app.models.Body;
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
}
