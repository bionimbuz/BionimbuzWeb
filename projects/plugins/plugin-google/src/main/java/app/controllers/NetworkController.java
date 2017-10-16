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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.ImmutableList;

import app.common.GoogleComputeEngineUtils;
import app.common.Response;
import app.common.SystemConstants;
import app.models.FirewallModel;

@RestController
public class NetworkController {  
    
    /*
     * Temp Methods
     */
    @RequestMapping(path = "/rule/delete", method = RequestMethod.GET)
    public Object delete() {
        
        FirewallModel firewallRule = 
                new FirewallModel(
                        FirewallModel.PROTOCOL.tcp, 
                        80, 
                        new ArrayList<>());
        
        return rule(firewallRule.getName());
    }
    
    @RequestMapping(path = "/rule/create", method = RequestMethod.GET)
    public Object create() {
        
        List<String> lstRanges = new ArrayList<>();
        lstRanges.add("0.0.0.0/0");
//        lstRanges.add("10.0.0.0/8");
        
        FirewallModel firewallRule = 
                new FirewallModel(
                        FirewallModel.PROTOCOL.tcp, 
                        80, 
                        lstRanges);
        
        return rule(firewallRule);
    }
    
    /*
     * Controller Methods
     */
    @RequestMapping(path = "/rule", method = RequestMethod.POST)
    public Response rule(
            @RequestParam(value = "firewall") FirewallModel firewall) {
        try {
            GoogleComputeEngineApi googleApi = GoogleComputeEngineUtils.createApi();  
            configureFirewallRule(googleApi, firewall);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (new Response(Response.Type.SUCCESS, "Success!!"));
    }
    
    @RequestMapping(path = "/rule", method = RequestMethod.DELETE)
    public Response rule(
            @RequestParam(value = "name") String name) {
        try {
            GoogleComputeEngineApi googleApi = GoogleComputeEngineUtils.createApi();  

            FirewallApi firewallApi = googleApi.firewalls();
                    
            if(firewallApi.get(name) != null) {            
                Operation operation = firewallApi.delete(name);
                GoogleComputeEngineUtils.waitOperation(googleApi, operation);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (new Response(Response.Type.SUCCESS, "Success."));
    }
    
    @RequestMapping(path = "/rules", method = RequestMethod.GET)
    public Object rules() {
        try {
            GoogleComputeEngineApi googleApi = GoogleComputeEngineUtils.createApi();  
            URI networkURL = GoogleComputeEngineUtils.assertDefaultNetwork(googleApi);
            FirewallApi firewallApi = googleApi.firewalls();
                        
            List<FirewallModel> res = new ArrayList<>();
            
            Iterator<ListPage<Firewall>> listPages = firewallApi.list();
            while (listPages.hasNext()) {
                ListPage<Firewall> firewalls = listPages.next();
                for (Firewall firewall : firewalls) {                         
                    FirewallModel firewallModel = 
                            createFirewallModel(firewall);
                    if(firewallModel != null) {
                        res.add(firewallModel);
                    }
                }
            }     
            return res;            
        } catch (Exception e) {
            e.printStackTrace();
            return (new Response(Response.Type.ERROR, e.getMessage()));
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
    
    private void configureFirewallRule(
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
