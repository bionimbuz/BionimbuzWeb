package app.common;

import java.net.URI;

import org.jclouds.ContextBuilder;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.domain.Credentials;
import org.jclouds.googlecloud.GoogleCredentialsFromJson;
import org.jclouds.googlecomputeengine.GoogleComputeEngineApi;
import org.jclouds.googlecomputeengine.GoogleComputeEngineProviderMetadata;
import org.jclouds.googlecomputeengine.domain.Operation;
import org.jclouds.googlecomputeengine.features.NetworkApi;
import org.jclouds.googlecomputeengine.features.OperationApi;

import com.google.common.base.Supplier;
import com.google.inject.Injector;

public class GoogleComputeEngineUtils {    
    
    public static GoogleComputeEngineApi createApi(final String credential) {
        try {
            Supplier<Credentials> credentialSupplier = new GoogleCredentialsFromJson(
                    credential);

            ComputeServiceContext context = ContextBuilder
                    .newBuilder(SystemConstants.CLOUD_TYPE)
                    .credentialsSupplier(credentialSupplier)
                    .buildView(ComputeServiceContext.class);

            Credentials credentials = credentialSupplier.get();
            ContextBuilder contextBuilder = ContextBuilder
                    .newBuilder(GoogleComputeEngineProviderMetadata.builder()
                            .build())
                    .credentials(credentials.identity, credentials.credential);

            Injector injector = contextBuilder.buildInjector();
            return injector.getInstance(GoogleComputeEngineApi.class);
            
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    public static URI assertDefaultNetwork(GoogleComputeEngineApi googleApi)
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
            GoogleComputeEngineApi googleApi, Operation operation) {
        OperationApi operationsApi = googleApi.operations();
        ThreadHelper.waitForOperation(operationsApi, operation);
    }
}
