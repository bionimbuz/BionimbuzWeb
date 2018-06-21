package app.controllers;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.google.common.net.HttpHeaders;

import app.common.HttpHeadersCustom;
import app.common.Routes;
import app.models.Body;
import app.models.PluginImageModel;

public abstract class AbstractImageController extends BaseController {
    protected static final Logger LOGGER = LoggerFactory.getLogger(AbstractImageController.class);  

    /*
     * Action Methods
     */
    
    @RequestMapping(path = Routes.IMAGES, method = RequestMethod.GET)
    private ResponseEntity<Body<List<PluginImageModel>>> listImagesAction(
            @RequestHeader(value=HttpHeadersCustom.API_VERSION) final String version,
            @RequestHeader(value=HttpHeaders.AUTHORIZATION) final String token, 
            @RequestHeader(value=HttpHeadersCustom.AUTHORIZATION_ID) final String identity) {
        return callImplementedMethod("listImages", version, token, identity);        
    }     
    @RequestMapping(path = Routes.IMAGES_NAME_, method = RequestMethod.GET)
    private ResponseEntity< Body<PluginImageModel> > getImageAction(
            @RequestHeader(value=HttpHeadersCustom.API_VERSION) final String version,
            @RequestHeader(value=HttpHeaders.AUTHORIZATION) final String token, 
            @RequestHeader(value=HttpHeadersCustom.AUTHORIZATION_ID) final String identity, 
            @PathVariable(value="name") final String name) {  
        return callImplementedMethod("getImage", version, token, identity, name);   
    }   

    /*
     * Abstract Methods
     */
    
    protected abstract ResponseEntity<Body<List<PluginImageModel>>> listImages(
            final String token, 
            final String identity) throws Exception;
    protected abstract ResponseEntity<Body<PluginImageModel>> getImage(
            final String token, 
            final String identity,
            final String name) throws Exception;  

}
