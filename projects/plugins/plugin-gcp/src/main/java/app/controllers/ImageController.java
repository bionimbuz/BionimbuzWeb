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
import app.models.PluginImageModel;

@RestController
public class ImageController extends AbstractImageController{ 

    /*
     * Overwritten Methods
     */
    
    @Override
    protected ResponseEntity<Body<PluginImageModel>> getImage(
            final String token, final String identity, final String name) throws Exception {
        
        try(GoogleComputeEngineApi googleApi = 
                GoogleComputeEngineUtils.createApi(
                        identity, 
                        token)) { 
            ImageApi api = googleApi.images();
            ListOptions options = new ListOptions();
            options.filter("name eq " + name);
            options.maxResults(1);

            List<PluginImageModel> res = searchImages(api, options); 
            
            PluginImageModel model = null;
            if(!res.isEmpty()) {
                model  = res.get(0);
            }            
            return ResponseEntity.ok(
                    Body.create(model));
        }
    }
    
    @Override
    protected ResponseEntity<Body<List<PluginImageModel>>> listImages(
            final String token, 
            final String identity) throws Exception  {     
        try(GoogleComputeEngineApi googleApi = 
                GoogleComputeEngineUtils.createApi(
                        identity, 
                        token)) { 
            ImageApi api = googleApi.images();
                        
            List<PluginImageModel> res = searchImages(api, null);
            return ResponseEntity.ok(
                    Body.create(res)); 
        }
    }
    
    private List<PluginImageModel> searchImages(ImageApi api, final ListOptions options){
        List<PluginImageModel> res = new ArrayList<>();
        
        Iterator<ListPage<Image>> listPages = 
                api.listInProject(
                        SystemConstants.IMAGE_PROJECT_UBUNTU);
        while (listPages.hasNext()) {
            ListPage<Image> images = listPages.next();
            for (Image image : images) {                         
                PluginImageModel model = createImageModel(image);
                if(model != null) {
                    res.add(model);
                }
            }
        }  
        
        return res;
    }
    
    private PluginImageModel createImageModel(final Image image) {
        PluginImageModel res = new PluginImageModel(
                image.id(), 
                image.name(), 
                image.selfLink().toString());                
        return res;
    }
}
