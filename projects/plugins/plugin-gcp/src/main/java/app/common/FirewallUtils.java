package app.common;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.jclouds.googlecomputeengine.GoogleComputeEngineApi;
import org.jclouds.googlecomputeengine.domain.Firewall;
import org.jclouds.googlecomputeengine.domain.Operation;
import org.jclouds.googlecomputeengine.features.FirewallApi;
import org.jclouds.googlecomputeengine.options.FirewallOptions;

import com.google.common.collect.ImmutableList;

import app.models.PluginFirewallModel;
import app.models.PluginFirewallModel.PROTOCOL;
import app.models.PluginInstanceModel;

public class FirewallUtils {  

    public static void createRulesForInstances(GoogleComputeEngineApi googleApi, final List<PluginInstanceModel> instances) throws Exception {        
        PluginFirewallModel firewall;
        // TODO: implement range list 
        List<String> range = new ArrayList<>();
        
        // First, TCP ports
        Set<Integer> ports = new TreeSet<>();        
        for(PluginInstanceModel instance : instances) {
            ports.addAll(instance.getFirewallTcpPorts());
        }
        for(Integer port : ports) {
            firewall = new PluginFirewallModel(PROTOCOL.tcp, port, range);
            replaceFirewallRule(googleApi, firewall);
        }
        
        // Second, UDP ports
        ports = new TreeSet<>();        
        for(PluginInstanceModel instance : instances) {
            ports.addAll(instance.getFirewallUdpPorts());
        }
        for(Integer port : ports) {
            firewall = new PluginFirewallModel(PROTOCOL.udp, port, range);
            replaceFirewallRule(googleApi, firewall);
        }
    }
    
    public static void replaceFirewallRule(
            GoogleComputeEngineApi googleApi, 
            PluginFirewallModel firewallRule
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
