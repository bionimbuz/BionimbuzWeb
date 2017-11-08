package app.controllers;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.google.common.net.HttpHeaders;

import app.common.HttpHeadersCustom;
import app.common.Routes;
import app.common.exceptions.VersionException;
import app.models.Body;
import app.models.InstanceModel;

public abstract class AbstractInstanceController extends BaseController {
    protected static final Logger LOGGER = LoggerFactory.getLogger(AbstractInstanceController.class);  

    /*
     * Action Methods
     */
    
    @RequestMapping(path = Routes.INSTANCES, method = RequestMethod.POST)
    private ResponseEntity<Body<List<InstanceModel>>> createInstanceAction(
            @RequestHeader(value=HttpHeadersCustom.API_VERSION) final String version,
            @RequestHeader(value=HttpHeaders.AUTHORIZATION) final String token, 
            @RequestHeader(value=HttpHeadersCustom.AUTHORIZATION_ID) final String identity, 
            @RequestBody List<InstanceModel> listModel) {  
        HttpStatus errorStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        String errorMessage = "";
        try {
            assertVersionSupported(version);
            return createInstance(token, identity, listModel);
        } catch (VersionException e) {
            LOGGER.error(e.getMessage(), e);
            errorStatus = HttpStatus.MOVED_PERMANENTLY;
            errorMessage = e.getMessage();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            errorMessage = e.getMessage();
        }
        return ResponseEntity
                .status(errorStatus)
                .body(new Body<List<InstanceModel>>(errorMessage)); 
    }
    
    @RequestMapping(path = Routes.INSTANCES_ZONE_NAME, method = RequestMethod.GET)
    private ResponseEntity<Body<InstanceModel>> getInstanceAction(
            @RequestHeader(value=HttpHeadersCustom.API_VERSION) final String version,
            @RequestHeader(value=HttpHeaders.AUTHORIZATION) final String token, 
            @RequestHeader(value=HttpHeadersCustom.AUTHORIZATION_ID) final String identity, 
            @PathVariable(value = "zone") final String zone,
            @PathVariable(value = "name") final String name) {
        HttpStatus errorStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        String errorMessage = "";
        try {
            assertVersionSupported(version);
            return getInstance(token, identity, zone, name);
        } catch (VersionException e) {
            LOGGER.error(e.getMessage(), e);
            errorStatus = HttpStatus.MOVED_PERMANENTLY;
            errorMessage = e.getMessage();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            errorMessage = e.getMessage();
        }
        return ResponseEntity
                .status(errorStatus)
                .body(new Body<InstanceModel>(errorMessage)); 
    }
    
    @RequestMapping(path = Routes.INSTANCES_ZONE_NAME, method = RequestMethod.DELETE)
    private ResponseEntity<Body<Void>> deleteInstanceAction(
            @RequestHeader(value=HttpHeadersCustom.API_VERSION) final String version,
            @RequestHeader(value=HttpHeaders.AUTHORIZATION) final String token, 
            @RequestHeader(value=HttpHeadersCustom.AUTHORIZATION_ID) final String identity, 
            @PathVariable(value = "zone") final String zone,
            @PathVariable(value = "name") final String name) {   
        HttpStatus errorStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        String errorMessage = "";
        try {
            assertVersionSupported(version);
            return deleteInstance(token, identity, zone, name);      
        } catch (VersionException e) {
            LOGGER.error(e.getMessage(), e);
            errorStatus = HttpStatus.MOVED_PERMANENTLY;
            errorMessage = e.getMessage();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            errorMessage = e.getMessage();
        }
        return ResponseEntity
                .status(errorStatus)
                .body(new Body<Void>(errorMessage)); 
    }
   
    @RequestMapping(path = Routes.INSTANCES, method = RequestMethod.GET)
    private ResponseEntity<Body<List<InstanceModel>>> listInstancesAction(
            @RequestHeader(value=HttpHeadersCustom.API_VERSION) final String version,
            @RequestHeader(value=HttpHeaders.AUTHORIZATION) final String token, 
            @RequestHeader(value=HttpHeadersCustom.AUTHORIZATION_ID) final String identity) {
        HttpStatus errorStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        String errorMessage = "";
        try {
            assertVersionSupported(version);      
            return listInstances(token, identity);
        } catch (VersionException e) {
            LOGGER.error(e.getMessage(), e);
            errorStatus = HttpStatus.MOVED_PERMANENTLY;
            errorMessage = e.getMessage();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            errorMessage = e.getMessage();
        }
        return ResponseEntity
                .status(errorStatus)
                .body(new Body<List<InstanceModel>>(errorMessage)); 
    }

    /*
     * Abstract Methods
     */
    
    protected abstract ResponseEntity<Body<List<InstanceModel>>> createInstance(
            final String token, 
            final String identity,
            List<InstanceModel> listModel) throws Exception;    
    protected abstract ResponseEntity<Body<InstanceModel>> getInstance(
            final String token, 
            final String identity,
            final String zone,
            final String name) throws Exception;    
    protected abstract ResponseEntity<Body<Void>> deleteInstance(
            final String token, 
            final String identity,
            final String zone,
            final String name) throws Exception;   
    protected abstract ResponseEntity<Body<List<InstanceModel>>> listInstances(
            final String token, 
            final String identity) throws Exception;

}
