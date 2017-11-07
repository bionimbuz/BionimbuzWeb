package app.common;

import java.net.URI;
import java.util.Properties;

import org.jclouds.ContextBuilder;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.googlecomputeengine.GoogleComputeEngineApi;
import org.jclouds.googlecomputeengine.domain.Operation;
import org.jclouds.googlecomputeengine.features.NetworkApi;
import org.jclouds.googlecomputeengine.features.OperationApi;
import org.jclouds.oauth.v2.config.CredentialType;
import org.jclouds.oauth.v2.config.OAuthProperties;

public class GoogleComputeEngineUtils {    
        
    public static GoogleComputeEngineApi createApi(final String identity, String token) throws Exception {   
        
        if(token.startsWith(HttpHeaders.HEADER_VALUE_AUTHORIZATION_BEARER)) {
            token = token.replaceFirst(HttpHeaders.HEADER_VALUE_AUTHORIZATION_BEARER, "");
        }
        
        Properties overrides = new Properties();
        overrides.put(OAuthProperties.CREDENTIAL_TYPE,
                CredentialType.BEARER_TOKEN_CREDENTIALS.toString());

        ContextBuilder builder = ContextBuilder
                .newBuilder(SystemConstants.CLOUD_TYPE);

        ComputeServiceContext context = builder.overrides(overrides)
                .credentials(identity, token)
                .buildView(ComputeServiceContext.class);

        GoogleComputeEngineApi googleApi = context
                .unwrapApi(GoogleComputeEngineApi.class);
        return googleApi;        
    }       
    
    public static URI assertDefaultNetwork(final GoogleComputeEngineApi googleApi)
            throws Exception {
        NetworkApi networkApi = googleApi.networks();
        URI networkURL = networkApi.get(GlobalConstants.DEFAULT_NETWORK)
                .selfLink();
        if (networkURL == null) {
            throw new Exception(
                    "Your project does not have a default network. Please recreate the default network or try again with a new project");
        }
        return networkURL;
    }
    
    public static void waitOperation(
            final GoogleComputeEngineApi googleApi, final Operation operation) {
        OperationApi operationsApi = googleApi.operations();
        ThreadHelper.waitForOperation(operationsApi, operation);
    }
}
