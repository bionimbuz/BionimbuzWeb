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
import app.models.Body;
import app.models.FirewallModel;

@RestController
public class FirewallController extends AbstractFirewallController{  

    /*
     * Overwritten Methods
     */
    
    @Override
    protected ResponseEntity<Body<FirewallModel>> replaceRule(
            final String token, 
            final String identity,
            FirewallModel model) throws Exception {        
        try(GoogleComputeEngineApi googleApi = 
                GoogleComputeEngineUtils.createApi(
                        identity, 
                        token)) { 
            replaceFirewallRule(googleApi, model);
            return ResponseEntity.ok(
                    Body.create(model));
        }
    }

    @Override
    protected ResponseEntity<Body<FirewallModel>> getRule(
            final String token, 
            final String identity,
    		final String name) throws Exception {     
        try(GoogleComputeEngineApi googleApi = 
                GoogleComputeEngineUtils.createApi(
                        identity, 
                        token)) { 
            FirewallApi firewallApi = googleApi.firewalls();
                  
            Firewall firewall = firewallApi.get(name);
            if(firewall == null) { 
                return new ResponseEntity<>(HttpStatus.NOT_FOUND); 
            }
            
            FirewallModel model = createFirewallModel(firewall);  
            return ResponseEntity.ok(
                    Body.create(model));
        }
    }

    @Override
    protected ResponseEntity<Body<Void>> deleteRule(
            final String token, 
            final String identity,
    		final String name) throws Exception  {     
        try(GoogleComputeEngineApi googleApi = 
                GoogleComputeEngineUtils.createApi(
                        identity, 
                        token)) { 
            
            FirewallApi firewallApi = googleApi.firewalls();                    
            Firewall firewall = firewallApi.get(name);
            if(firewall == null) {   
                return new ResponseEntity<>(HttpStatus.NOT_FOUND); 
            }
                      
            Operation operation = firewallApi.delete(name);
            GoogleComputeEngineUtils.waitOperation(googleApi, operation);            
            return new ResponseEntity<>(HttpStatus.OK);
        }
    }

    @Override
    protected ResponseEntity<Body<List<FirewallModel>>> listRules(
            final String token, 
            final String identity) throws Exception  {     
        try(GoogleComputeEngineApi googleApi = 
                GoogleComputeEngineUtils.createApi(
                        identity, 
                        token)) { 
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
            return ResponseEntity.ok(
                    Body.create(res)); 
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
