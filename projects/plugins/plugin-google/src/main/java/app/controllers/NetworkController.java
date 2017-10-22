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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.ImmutableList;

import app.common.GoogleComputeEngineUtils;
import app.common.Response;
import app.common.Routes;
import app.common.SystemConstants;
import app.models.FirewallModel;

@RestController
public class NetworkController {  

    /*
     * Controller Methods
     */
	
    @RequestMapping(path = Routes.NETWORK_RULE, method = RequestMethod.POST)
    public Response<?> replaceRule(
            @RequestBody FirewallModel firewall) {        
        try {
            GoogleComputeEngineApi googleApi = GoogleComputeEngineUtils.createApi();  
            replaceFirewallRule(googleApi, firewall);
        } catch (Exception e) {
            e.printStackTrace();
            return Response.error(e.getMessage());
        }
        return Response.success();
    }
    
	@RequestMapping(path = Routes.NETWORK_RULE+"/{name}", method = RequestMethod.GET)
    public Response<FirewallModel> getRule(
    		@PathVariable(value="name") final String name) {
        try {
            GoogleComputeEngineApi googleApi = GoogleComputeEngineUtils.createApi();  
            FirewallApi firewallApi = googleApi.firewalls();
                  
            Firewall firewall = firewallApi.get(name);
            if(firewall == null) {   
                return Response.error("Object not found.");
            }
            
            FirewallModel model = createFirewallModel(firewall);            
            return Response.success(model);     
        } catch (Exception e) {
            e.printStackTrace();
            return Response.error(e.getMessage());
        }
    }
    
    @RequestMapping(path = Routes.NETWORK_RULE+"/{name}", method = RequestMethod.DELETE)
    public Response<?> deleteRule(
    		@PathVariable(value="name") final String name) {
        try {
            GoogleComputeEngineApi googleApi = GoogleComputeEngineUtils.createApi(); 
            FirewallApi firewallApi = googleApi.firewalls();
                    
            Firewall firewall = firewallApi.get(name);
            if(firewall == null) {   
                return Response.error("Object not found.");
            }
                      
            Operation operation = firewallApi.delete(name);
            GoogleComputeEngineUtils.waitOperation(googleApi, operation);
        } catch (Exception e) {
            e.printStackTrace();
            return Response.error(e.getMessage());
        }
        return Response.success();
    }
    
    @RequestMapping(path = Routes.NETWORK_RULES, method = RequestMethod.GET)
    public Response<List<FirewallModel>> listRules() {
        try {
            GoogleComputeEngineApi googleApi = GoogleComputeEngineUtils.createApi();  
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
            return Response.success(res);            
        } catch (Exception e) {
            e.printStackTrace();
            return Response.error(e.getMessage());
        }
    }
    
    /*
     * Class Methods
     */
    private FirewallModel createFirewallModel(final Firewall firewall) {
        
        if(!firewall.name().startsWith(SystemConstants.BNZ_FIREWALL))
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
