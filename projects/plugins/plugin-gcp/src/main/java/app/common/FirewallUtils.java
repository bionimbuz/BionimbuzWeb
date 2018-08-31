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

import app.models.PluginComputingInstanceModel;

public class FirewallUtils {  
    
    public static enum PROTOCOL {
        tcp,
        udp
    }

    public static void createRulesForInstance(
            GoogleComputeEngineApi googleApi, 
            final PluginComputingInstanceModel instance) throws Exception {        
        // TODO: implement range list 
        List<String> range = new ArrayList<>();
        
        // First, TCP ports
        Set<Integer> ports = new TreeSet<>();        
        ports.addAll(instance.getFirewallTcpPorts());
        for(Integer port : ports) {
            replaceFirewallRule(googleApi, PROTOCOL.tcp, port, range);
        }
        
        // Second, UDP ports
        ports = new TreeSet<>();        
        ports.addAll(instance.getFirewallUdpPorts());
        for(Integer port : ports) {
            replaceFirewallRule(googleApi, PROTOCOL.udp, port, range);
        }
    }
    
    public static void replaceFirewallRule(
            GoogleComputeEngineApi googleApi, 
            PROTOCOL protocol,
            Integer port,
            List<String> range) throws Exception {

        URI networkURL = GoogleComputeEngineUtils.assertDefaultNetwork(googleApi);

        Firewall.Rule rule = Firewall.Rule.create(
                protocol.toString(), 
                ImmutableList.of(String.valueOf(port)));
        
        FirewallOptions options = new FirewallOptions()
                .addAllowedRule(rule)
                .sourceRanges(range);
        
        FirewallApi firewallApi = googleApi.firewalls();

        String name = generateName(protocol, port);
        Operation operation;        
        Firewall firewall = firewallApi.get(name);
        if(firewall != null) {        
            operation = firewallApi.update(
                    name, options);
        } else {        
            operation = firewallApi.createInNetwork(
                    name, networkURL, options);  
        }
        GoogleComputeEngineUtils.waitOperation(googleApi, operation);
    }    
    
    public static String generateName(PROTOCOL protocol, Integer port) {
        return GlobalConstants.BNZ_FIREWALL + "-" + protocol + "-" + String.valueOf(port); 
    }    
}
