package app.controllers;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import app.common.Routes;
import app.models.CredentialModel;
import app.models.InstanceModel;

public abstract class AbstractInstanceController {
    protected static final Logger LOGGER = LoggerFactory.getLogger(AbstractInstanceController.class);  

    /*
     * Action Methods
     */
    
    @RequestMapping(path = Routes.INSTANCE, method = RequestMethod.POST)
    private ResponseEntity<?> createInstanceAction(
            @RequestBody CredentialModel<List<InstanceModel>> credential) {  
        return createInstance(credential);
    }
    
    @RequestMapping(path = Routes.INSTANCE_ZONE_NAME, method = RequestMethod.POST)
    private ResponseEntity<?> getInstanceAction(
            @PathVariable(value = "zone") final String zone,
            @PathVariable(value = "name") final String name,
            @RequestBody CredentialModel<Void> credential) {
        return getInstance(zone, name, credential);
    }
    
    @RequestMapping(path = Routes.INSTANCE_ZONE_NAME, method = RequestMethod.DELETE)
    private ResponseEntity<?> deleteInstanceAction(
            @PathVariable(value = "zone") final String zone,
            @PathVariable(value = "name") final String name,
            @RequestBody CredentialModel<Void> credential) {    
        return deleteInstance(zone, name, credential);
    }
   
    @RequestMapping(path = Routes.INSTANCES, method = RequestMethod.POST)
    private ResponseEntity<?> listInstancesAction(
            @RequestBody CredentialModel<Void> credential) {       
        return listInstances(credential);
    }

    /*
     * Abstract Methods
     */
    
    protected abstract ResponseEntity<?> createInstance(
            CredentialModel<List<InstanceModel>> credential);    
    protected abstract ResponseEntity<?> getInstance(
            final String zone,
            final String name,
            CredentialModel<Void> credential);    
    protected abstract ResponseEntity<?> deleteInstance(
            final String zone,
            final String name,
            CredentialModel<Void> credential);   
    protected abstract ResponseEntity<?> listInstances(
            CredentialModel<Void> credential);

}
