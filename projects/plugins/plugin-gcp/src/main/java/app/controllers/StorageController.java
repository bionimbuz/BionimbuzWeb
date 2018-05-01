package app.controllers;

import java.io.IOException;

import org.jclouds.googlecloud.config.CurrentProject;
import org.jclouds.googlecloudstorage.GoogleCloudStorageApi;
import org.jclouds.googlecloudstorage.domain.Bucket;
import org.jclouds.googlecloudstorage.domain.DomainResourceReferences.Location;
import org.jclouds.googlecloudstorage.domain.DomainResourceReferences.StorageClass;
import org.jclouds.googlecloudstorage.domain.templates.BucketTemplate;
import org.jclouds.googlecloudstorage.features.BucketApi;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.net.HttpHeaders;

import app.common.GoogleCloudStorageUtils;
import app.common.HttpHeadersCustom;

@RestController
public class StorageController {

    @RequestMapping(path = "/buckets", method = RequestMethod.POST)
    public ResponseEntity< Void > createBucket(
            @RequestHeader(value=HttpHeadersCustom.API_VERSION) final String version,
            @RequestHeader(value=HttpHeaders.AUTHORIZATION) final String token,
            @RequestHeader(value=HttpHeadersCustom.AUTHORIZATION_ID) final String identity) {

        try(GoogleCloudStorageApi googleApi =
                GoogleCloudStorageUtils.createApi(
                        identity,
                        token)) {
            // TODO: make dynamic location
            Location location = Location.US_CENTRAL1;

            String projectId =
                    CurrentProject.ClientEmail.toProjectNumber(identity);
            BucketApi bucketApi = googleApi.getBucketApi();
            BucketTemplate templategoogleApi =
                    new BucketTemplate().name(projectId + "_" + location.toString().toLowerCase())
                        .location(location)
                        .storageClass(StorageClass.STANDARD);
            Bucket bucket = bucketApi.createBucket(projectId, templategoogleApi);
            if (bucket == null) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return new ResponseEntity<>(HttpStatus.MOVED_PERMANENTLY);
    }
}
