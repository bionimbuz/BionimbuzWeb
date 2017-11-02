package app.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import app.common.Routes;
import app.models.CredentialModel;
import app.models.FirewallModel;

public abstract class AbstractFirewallController {  
    protected static final Logger LOGGER = LoggerFactory.getLogger(AbstractFirewallController.class);  

    /*
     * Action Methods
     */
	
    @RequestMapping(path = Routes.FIREWALL, method = RequestMethod.POST)
    private ResponseEntity<?> replaceRuleAction(
            @RequestBody CredentialModel<FirewallModel> credential) { 
        return replaceRule(credential);
    }    
	@RequestMapping(path = Routes.FIREWALL_NAME, method = RequestMethod.POST)
	private ResponseEntity<?> getRuleAction(
    		@PathVariable(value="name") final String name,
            @RequestBody CredentialModel<Void> credential) {   
	    return getRule(name, credential);
    }    
    @RequestMapping(path = Routes.FIREWALL_NAME, method = RequestMethod.DELETE)
    private ResponseEntity<?> deleteRuleAction(
    		@PathVariable(value="name") final String name,
            @RequestBody CredentialModel<Void> credential) {
        return deleteRule(name, credential);        
    }    
    @RequestMapping(path = Routes.FIREWALLS, method = RequestMethod.POST)
    private ResponseEntity<?> listRulesAction(
            @RequestBody CredentialModel<Void> credential) {
        return listRules(credential);
    }  
    
    /*
     * Abstract Methods
     */
    
    protected abstract ResponseEntity<?> replaceRule(
            CredentialModel<FirewallModel> credential);    
    protected abstract ResponseEntity<?> getRule(
            final String name,
            CredentialModel<Void> credential);    
    protected abstract ResponseEntity<?> deleteRule(
            final String name,
            CredentialModel<Void> credential);    
    protected abstract ResponseEntity<?> listRules(
            CredentialModel<Void> credential);  
}
