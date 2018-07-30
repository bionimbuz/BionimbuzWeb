package app.controllers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.jclouds.ec2.EC2Api;
import org.jclouds.ec2.domain.Image;
import org.jclouds.ec2.features.AMIApi;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;

import app.common.AWSEC2Utils;
import app.models.Body;
import app.models.PluginImageModel;

@RestController
public class ImageController extends AbstractImageController{ 

    private static final String DEFAULT_IMAGE_REGION = "us-east-1";
    private static final String UBUNTU_IMAGES_FILTER = "ubuntu/images/hvm-ssd/ubuntu-*";    
    private static Multimap<String, String> createFilter(final String imageName) {
        return ImmutableMultimap.<String, String> builder()//
                .put("virtualization-type", "hvm")//
                .put("architecture", "x86_64")//
                .putAll("owner-id", ImmutableSet.<String> of("099720109477"))//
                .put("hypervisor", "xen")//
                .put("state", "available")//
                .put("image-type", "machine")//
                .put("root-device-name", "/dev/sda1")//
                .put("root-device-type", "ebs")//
                .put("is-public", "true")
                .put("name", imageName)
                .build();
    }
    
    /*
     * Overwritten Methods
     */
    
    @Override
    protected ResponseEntity<Body<PluginImageModel>> getImage(
            final String token, final String identity, final String name) throws Exception {

        try(EC2Api awsApi =
                AWSEC2Utils.createApi(
                        identity,
                        token)) {            
            Image image = 
                    searchFirstImageWithNameFilter(
                            awsApi, "*"+name);            
            if(image == null) {
                return new ResponseEntity<>(
                        Body.create(null),
                        HttpStatus.NOT_FOUND);
            }     
            return ResponseEntity.ok(
                    Body.create(createImageModel(image)));                 
        } 
    }        
    
    @Override
    protected ResponseEntity<Body<List<PluginImageModel>>> listImages(
            final String token, 
            final String identity) throws Exception  {   
        
        try(EC2Api awsApi =
                AWSEC2Utils.createApi(
                        identity,
                        token)) {
            
            Set<? extends Image> images = 
                    searchImagesWithNameFilter(
                            awsApi, UBUNTU_IMAGES_FILTER);
            
            List<PluginImageModel> res = new ArrayList<>();
            for (Image image : images) {
                res.add(createImageModel(image));
            }
            
            Collections.sort(res, new Comparator<PluginImageModel>(){
                @Override
                public int compare(PluginImageModel o1, PluginImageModel o2) {
                    return o1.getName().compareTo(o2.getName());
                }
            });

            return ResponseEntity.ok(
                    Body.create(res));                 
        }        
    }

    public static Image searchFirstImageWithNameFilter(
            final EC2Api awsApi, 
            final String nameFilter){
        Set<? extends Image> images = 
                searchImagesWithNameFilter(
                        awsApi, "*"+nameFilter);        
        if(images.isEmpty()) {
            return null;
        }        
        return images.iterator().next();
    }            
    
    private static Set<? extends Image> searchImagesWithNameFilter(
            final EC2Api awsApi, 
            final String nameFilter){        
        Optional<? extends AMIApi> apiAMI = 
                awsApi.getAMIApiForRegion(DEFAULT_IMAGE_REGION);          
        return apiAMI.get().describeImagesInRegionWithFilter(
                        DEFAULT_IMAGE_REGION, 
                        createFilter(nameFilter)); 
    }        
    
    private String getImageName(final String imageName) {
        if (imageName == null) {
            return "";
        }
        int i = imageName.lastIndexOf('/');
        String ext = i > 0 ? imageName.substring(i + 1) : "";
        return ext;
    }
    
    private PluginImageModel createImageModel(final Image image) {
        PluginImageModel res = new PluginImageModel(
                image.getId(), 
                getImageName(image.getName()), 
                image.getName());                
        return res;
    }
}
