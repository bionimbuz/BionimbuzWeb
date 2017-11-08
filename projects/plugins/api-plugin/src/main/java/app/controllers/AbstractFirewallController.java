package app.controllers;

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
import app.models.FirewallModel;

public abstract class AbstractFirewallController extends BaseController{  
    protected static final Logger LOGGER = LoggerFactory.getLogger(AbstractFirewallController.class);  

    /*
     * Action Methods
     */
	
    @RequestMapping(path = Routes.FIREWALLS, method = RequestMethod.POST)
    private ResponseEntity<?> replaceRuleAction(
            @RequestHeader(value=HttpHeadersCustom.API_VERSION) final String version,
            @RequestHeader(value=HttpHeaders.AUTHORIZATION) final String token, 
            @RequestHeader(value=HttpHeadersCustom.AUTHORIZATION_ID) final String identity, 
            @RequestBody FirewallModel model) { 
        HttpStatus errorStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        String errorMessage = "";
        try {
            assertVersionSupported(version);
            return replaceRule(token, identity, model);
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
                .body(errorMessage);
    }
    
	@RequestMapping(path = Routes.FIREWALLS_NAME, method = RequestMethod.GET)
	private ResponseEntity<?> getRuleAction(
            @RequestHeader(value=HttpHeadersCustom.API_VERSION) final String version,
            @RequestHeader(value=HttpHeaders.AUTHORIZATION) final String token, 
            @RequestHeader(value=HttpHeadersCustom.AUTHORIZATION_ID) final String identity, 
    		@PathVariable(value="name") final String name) {    
        HttpStatus errorStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        String errorMessage = "";
        try {
            assertVersionSupported(version);
            return getRule(token, identity, name);
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
                .body(errorMessage);
    }    
    @RequestMapping(path = Routes.FIREWALLS_NAME, method = RequestMethod.DELETE)
    private ResponseEntity<?> deleteRuleAction(
            @RequestHeader(value=HttpHeadersCustom.API_VERSION) final String version,
            @RequestHeader(value=HttpHeaders.AUTHORIZATION) final String token,  
            @RequestHeader(value=HttpHeadersCustom.AUTHORIZATION_ID) final String identity, 
    		@PathVariable(value="name") final String name) {
        HttpStatus errorStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        String errorMessage = "";
        try {
            assertVersionSupported(version);
            return deleteRule(token, identity, name);  
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
                .body(errorMessage);
    }    
    @RequestMapping(path = Routes.FIREWALLS, method = RequestMethod.GET)
    private ResponseEntity<?> listRulesAction(
            @RequestHeader(value=HttpHeadersCustom.API_VERSION) final String version,
            @RequestHeader(value=HttpHeaders.AUTHORIZATION) final String token, 
            @RequestHeader(value=HttpHeadersCustom.AUTHORIZATION_ID) final String identity) {        
        HttpStatus errorStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        String errorMessage = "";
        try {
            assertVersionSupported(version);
            return listRules(token, identity);
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
                .body(errorMessage);
    }  
    
    /*
     * Abstract Methods
     */
    
    protected abstract ResponseEntity<?> replaceRule(
            final String token, 
            final String identity,
            FirewallModel model) throws Exception;    
    protected abstract ResponseEntity<?> getRule(
            final String token, 
            final String identity,
            final String name) throws Exception;    
    protected abstract ResponseEntity<?> deleteRule(
            final String token, 
            final String identity,
            final String name) throws Exception;    
    protected abstract ResponseEntity<?> listRules(
            final String token, 
            final String identity) throws Exception;  
}
