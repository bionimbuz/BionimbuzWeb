package app.controllers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jclouds.googlecloud.domain.ListPage;
import org.jclouds.googlecomputeengine.GoogleComputeEngineApi;
import org.jclouds.googlecomputeengine.domain.Image;
import org.jclouds.googlecomputeengine.features.ImageApi;
import org.jclouds.googlecomputeengine.options.ListOptions;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import app.common.GoogleComputeEngineUtils;
import app.common.SystemConstants;
import app.models.Body;
import app.models.ImageModel;

@RestController
public class ImageController extends AbstractImageController{ 

    /*
     * Overwritten Methods
     */
    
    @Override
    protected ResponseEntity<Body<ImageModel>> getImage(
            final String token, final String identity, final String name) throws Exception {
        
        try(GoogleComputeEngineApi googleApi = 
                GoogleComputeEngineUtils.createApi(
                        identity, 
                        token)) { 
            ImageApi api = googleApi.images();
            ListOptions options = new ListOptions();
            options.filter("name eq " + name);
            options.maxResults(1);

            List<ImageModel> res = searchImages(api, options); 
            
            ImageModel model = null;
            if(!res.isEmpty()) {
                model  = res.get(0);
            }            
            return ResponseEntity.ok(
                    Body.create(model));
        }
    }
    
    @Override
    protected ResponseEntity<Body<List<ImageModel>>> listImages(
            final String token, 
            final String identity) throws Exception  {     
        try(GoogleComputeEngineApi googleApi = 
                GoogleComputeEngineUtils.createApi(
                        identity, 
                        token)) { 
            ImageApi api = googleApi.images();
                        
            List<ImageModel> res = searchImages(api, null);
            return ResponseEntity.ok(
                    Body.create(res)); 
        }
    }
    
    private List<ImageModel> searchImages(ImageApi api, final ListOptions options){
        List<ImageModel> res = new ArrayList<>();
        
        Iterator<ListPage<Image>> listPages = 
                api.listInProject(
                        SystemConstants.IMAGE_PROJECT_UBUNTU);
        while (listPages.hasNext()) {
            ListPage<Image> images = listPages.next();
            for (Image image : images) {                         
                ImageModel model = createImageModel(image);
                if(model != null) {
                    res.add(model);
                }
            }
        }  
        
        return res;
    }
    
    private ImageModel createImageModel(final Image image) {
        ImageModel res = new ImageModel(
                image.id(), 
                image.name(), 
                image.selfLink().toString());                
        return res;
    }
}