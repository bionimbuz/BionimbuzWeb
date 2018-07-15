package app.common;

import org.jclouds.ContextBuilder;
import org.jclouds.aws.ec2.AWSEC2Api;
import org.jclouds.compute.ComputeServiceContext;

public class AWSEC2Utils {

    public static AWSEC2Api createApi(final String identity, String token) throws Exception {

        ContextBuilder builder = ContextBuilder
                .newBuilder(SystemConstants.CLOUD_COMPUTE_TYPE);

        ComputeServiceContext context = builder
                .credentials(identity, token)
                .buildView(ComputeServiceContext.class);

        AWSEC2Api googleApi = context
                .unwrapApi(AWSEC2Api.class);
        return googleApi;
    }
}
