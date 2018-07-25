package app.common;

import java.util.List;

import org.jclouds.ec2.EC2Api;
import org.jclouds.ec2.features.SecurityGroupApi;
import org.jclouds.net.domain.IpProtocol;

import app.models.PluginInstanceModel;

public class FirewallUtils {  
    
    private static final String DEFAULT_GROUP_NAME = "default";
    
    // TODO: inform IPv6 format
    private static final String DEFAULT_CIDR_IP = "0.0.0.0/0";
    
    public static void createRulesForInstance(
            final EC2Api awsApi,
            final PluginInstanceModel instance) throws Exception {      
        
        SecurityGroupApi api = 
                awsApi.getSecurityGroupApiForRegion(instance.getRegion()).get();        
        
        createRules(
                api,
                instance.getRegion(), 
                IpProtocol.TCP, 
                instance.getFirewallTcpPorts());
        createRules(
                api,
                instance.getRegion(), 
                IpProtocol.UDP, 
                instance.getFirewallUdpPorts());        
    }     
    
    private static void createRules(
            final SecurityGroupApi api,
            final String region, 
            final IpProtocol protocol, 
            final List<Integer> ports) {
        if(ports == null)
            return;
        for(Integer port : ports) {       
            api.authorizeSecurityGroupIngressInRegion(
                    region, 
                    DEFAULT_GROUP_NAME, 
                    protocol, 
                    port, port,
                    DEFAULT_CIDR_IP);
        }
    }
}
