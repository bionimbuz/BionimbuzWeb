package app.controllers;

import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jclouds.googlecloud.domain.ListPage;
import org.jclouds.googlecomputeengine.GoogleComputeEngineApi;
import org.jclouds.googlecomputeengine.domain.Firewall;
import org.jclouds.googlecomputeengine.domain.Operation;
import org.jclouds.googlecomputeengine.features.FirewallApi;
import org.jclouds.googlecomputeengine.options.FirewallOptions;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.ImmutableList;

import app.common.GlobalConstants;
import app.common.GoogleComputeEngineUtils;
import app.models.CredentialModel;
import app.models.FirewallModel;

@RestController
public class FirewallController extends AbstractFirewallController{  

    /*
     * Overwritten Methods
     */
    
	@Override
    protected ResponseEntity<?> replaceRule(
            CredentialModel<FirewallModel> credential) {        
        try {
            GoogleComputeEngineApi googleApi = 
                    GoogleComputeEngineUtils.createApi(credential.getCredential());  
            replaceFirewallRule(googleApi, credential.getModel());
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ResponseEntity
		            .status(HttpStatus.INTERNAL_SERVER_ERROR)
		            .body(e.getMessage());
        }
    }

    @Override
    protected ResponseEntity<?> getRule(
    		final String name,
            CredentialModel<Void> credential) {
        try {
            GoogleComputeEngineApi googleApi = 
                    GoogleComputeEngineUtils.createApi(credential.getCredential());  
            FirewallApi firewallApi = googleApi.firewalls();
                  
            Firewall firewall = firewallApi.get(name);
            if(firewall == null) { 
                return new ResponseEntity<>(HttpStatus.NOT_FOUND); 
            }
            
            FirewallModel model = createFirewallModel(firewall);  
            return ResponseEntity
		            .status(HttpStatus.OK)
		            .body(model); 
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ResponseEntity
		            .status(HttpStatus.INTERNAL_SERVER_ERROR)
		            .body(e.getMessage());
        }
    }

    @Override
    protected ResponseEntity<?> deleteRule(
    		final String name,
    		CredentialModel<Void> credential) {
        try {
            GoogleComputeEngineApi googleApi = 
                    GoogleComputeEngineUtils.createApi(credential.getCredential()); 
            FirewallApi firewallApi = googleApi.firewalls();
                    
            Firewall firewall = firewallApi.get(name);
            if(firewall == null) {   
                return new ResponseEntity<>(HttpStatus.NOT_FOUND); 
            }
                      
            Operation operation = firewallApi.delete(name);
            GoogleComputeEngineUtils.waitOperation(googleApi, operation);            
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ResponseEntity
		            .status(HttpStatus.INTERNAL_SERVER_ERROR)
		            .body(e.getMessage());
        }
    }

    @Override
    protected ResponseEntity<?> listRules(
            CredentialModel<Void> credential) {
        try {
            GoogleComputeEngineApi googleApi = 
                    GoogleComputeEngineUtils.createApi(credential.getCredential()); 
            URI networkURL = GoogleComputeEngineUtils.assertDefaultNetwork(googleApi);
            FirewallApi firewallApi = googleApi.firewalls();
                        
            List<FirewallModel> res = new ArrayList<>();
            
            Iterator<ListPage<Firewall>> listPages = firewallApi.list();
            while (listPages.hasNext()) {
                ListPage<Firewall> firewalls = listPages.next();
                for (Firewall firewall : firewalls) {                         
                    FirewallModel model = createFirewallModel(firewall);
                    if(model != null) {
                        res.add(model);
                    }
                }
            }             
            return ResponseEntity
		            .status(HttpStatus.OK)
		            .body(res);    
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ResponseEntity
		            .status(HttpStatus.INTERNAL_SERVER_ERROR)
		            .body(e.getMessage());
        }
    }
    
    /*
     * Specific Class Methods
     */
    
    private FirewallModel createFirewallModel(final Firewall firewall) {
        
        if(!firewall.name().startsWith(GlobalConstants.BNZ_FIREWALL))
            return null;                                        
        List<Firewall.Rule> rules = firewall.allowed();
        if(rules.size() <= 0)
            return null;                    
        Firewall.Rule rule = rules.get(0);                   
        if(rule.ports().size() <= 0)
            return null;   
        
        Integer port = Integer.parseInt(rule.ports().get(0));
        FirewallModel.PROTOCOL protocol = FirewallModel.PROTOCOL.valueOf(rule.ipProtocol());
        
        return new FirewallModel(
                        firewall.name(), 
                        protocol,
                        port,
                        firewall.sourceRanges(),
                        firewall.creationTimestamp());
    }
    
    private void replaceFirewallRule(
            GoogleComputeEngineApi googleApi, 
            FirewallModel firewallRule
                ) throws Exception {

        URI networkURL = GoogleComputeEngineUtils.assertDefaultNetwork(googleApi);

        Firewall.Rule rule = Firewall.Rule.create(
                firewallRule.getProtocol().toString(), 
                ImmutableList.of(String.valueOf(firewallRule.getPort())));
        
        FirewallOptions options = new FirewallOptions()
                .addAllowedRule(rule)
                .sourceRanges(firewallRule.getLstRanges());
        
        FirewallApi firewallApi = googleApi.firewalls();
                
        Operation operation;        
        Firewall firewall = firewallApi.get(
                firewallRule.getName());
        if(firewall != null) {        
            operation = firewallApi.update(
                    firewallRule.getName(), options);
        } else {        
            operation = firewallApi.createInNetwork(
                            firewallRule.getName(), networkURL, options);  
        }
        GoogleComputeEngineUtils.waitOperation(googleApi, operation);
    }    
}