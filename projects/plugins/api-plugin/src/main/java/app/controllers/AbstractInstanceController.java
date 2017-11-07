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

import app.common.HttpHeaders;
import app.common.Routes;
import app.models.InstanceModel;

public abstract class AbstractInstanceController {
    protected static final Logger LOGGER = LoggerFactory.getLogger(AbstractInstanceController.class);  

    /*
     * Action Methods
     */
    
    @RequestMapping(path = Routes.INSTANCES, method = RequestMethod.POST)
    private ResponseEntity<?> createInstanceAction(
            @RequestHeader(value=HttpHeaders.HEADER_AUTHORIZATION) final String token, 
            @RequestHeader(value=HttpHeaders.HEADER_AUTHORIZATION_ID) final String identity, 
            @RequestBody List<InstanceModel> listModel) {  
        return createInstance(token, identity, listModel);
    }
    
    @RequestMapping(path = Routes.INSTANCES_ZONE_NAME, method = RequestMethod.GET)
    private ResponseEntity<?> getInstanceAction(
            @RequestHeader(value=HttpHeaders.HEADER_AUTHORIZATION) final String token, 
            @RequestHeader(value=HttpHeaders.HEADER_AUTHORIZATION_ID) final String identity, 
            @PathVariable(value = "zone") final String zone,
            @PathVariable(value = "name") final String name) {
        return getInstance(token, identity, zone, name);
    }
    
    @RequestMapping(path = Routes.INSTANCES_ZONE_NAME, method = RequestMethod.DELETE)
    private ResponseEntity<?> deleteInstanceAction(
            @RequestHeader(value=HttpHeaders.HEADER_AUTHORIZATION) final String token, 
            @RequestHeader(value=HttpHeaders.HEADER_AUTHORIZATION_ID) final String identity, 
            @PathVariable(value = "zone") final String zone,
            @PathVariable(value = "name") final String name) {    
        return deleteInstance(token, identity, zone, name);
    }
   
    @RequestMapping(path = Routes.INSTANCES, method = RequestMethod.GET)
    private ResponseEntity<?> listInstancesAction(
            @RequestHeader(value=HttpHeaders.HEADER_AUTHORIZATION) final String token, 
            @RequestHeader(value=HttpHeaders.HEADER_AUTHORIZATION_ID) final String identity) {       
        return listInstances(token, identity);
    }

    /*
     * Abstract Methods
     */
    
    protected abstract ResponseEntity<?> createInstance(
            final String token, 
            final String identity,
            List<InstanceModel> listModel);    
    protected abstract ResponseEntity<?> getInstance(
            final String token, 
            final String identity,
            final String zone,
            final String name);    
    protected abstract ResponseEntity<?> deleteInstance(
            final String token, 
            final String identity,
            final String zone,
            final String name);   
    protected abstract ResponseEntity<?> listInstances(
            final String token, 
            final String identity);

}
