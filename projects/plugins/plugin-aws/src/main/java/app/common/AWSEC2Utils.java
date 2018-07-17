package app.common;

import org.jclouds.ContextBuilder;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.ec2.EC2Api;

public class AWSEC2Utils {
    
    public static ComputeServiceContext createContext(final String identity, String token) throws Exception {

        ContextBuilder builder = ContextBuilder
                .newBuilder(SystemConstants.CLOUD_COMPUTE_TYPE);

        ComputeServiceContext context = builder
                .credentials(identity, token)
                .buildView(ComputeServiceContext.class);
        
        return context;
    }

    public static EC2Api createApi(final String identity, String token) throws Exception {

        ComputeServiceContext context = 
                createContext(identity, token);

        EC2Api awsApi = context
                .unwrapApi(EC2Api.class);
        return awsApi;
    }
}
