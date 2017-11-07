package app.controllers;

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
import app.models.FirewallModel;

public abstract class AbstractFirewallController {  
    protected static final Logger LOGGER = LoggerFactory.getLogger(AbstractFirewallController.class);  

    /*
     * Action Methods
     */
	
    @RequestMapping(path = Routes.FIREWALL, method = RequestMethod.POST)
    private ResponseEntity<?> replaceRuleAction(
            @RequestHeader(value=HttpHeaders.HEADER_AUTHORIZATION) final String token, 
            @RequestHeader(value=HttpHeaders.HEADER_AUTHORIZATION_ID) final String identity, 
            @RequestBody FirewallModel model) { 
        return replaceRule(token, identity, model);
    }    
	@RequestMapping(path = Routes.FIREWALL_NAME, method = RequestMethod.POST)
	private ResponseEntity<?> getRuleAction(
            @RequestHeader(value=HttpHeaders.HEADER_AUTHORIZATION) final String token, 
            @RequestHeader(value=HttpHeaders.HEADER_AUTHORIZATION_ID) final String identity, 
    		@PathVariable(value="name") final String name) {   
	    return getRule(token, identity, name);
    }    
    @RequestMapping(path = Routes.FIREWALL_NAME, method = RequestMethod.DELETE)
    private ResponseEntity<?> deleteRuleAction(
            @RequestHeader(value=HttpHeaders.HEADER_AUTHORIZATION) final String token, 
            @RequestHeader(value=HttpHeaders.HEADER_AUTHORIZATION_ID) final String identity, 
    		@PathVariable(value="name") final String name) {
        return deleteRule(token, identity, name);        
    }    
    @RequestMapping(path = Routes.FIREWALLS, method = RequestMethod.POST)
    private ResponseEntity<?> listRulesAction(
            @RequestHeader(value=HttpHeaders.HEADER_AUTHORIZATION) final String token, 
            @RequestHeader(value=HttpHeaders.HEADER_AUTHORIZATION_ID) final String identity) {
        return listRules(token, identity);
    }  
    
    /*
     * Abstract Methods
     */
    
    protected abstract ResponseEntity<?> replaceRule(
            final String token, 
            final String identity,
            FirewallModel model);    
    protected abstract ResponseEntity<?> getRule(
            final String token, 
            final String identity,
            final String name);    
    protected abstract ResponseEntity<?> deleteRule(
            final String token, 
            final String identity,
            final String name);    
    protected abstract ResponseEntity<?> listRules(
            final String token, 
            final String identity);  
}
