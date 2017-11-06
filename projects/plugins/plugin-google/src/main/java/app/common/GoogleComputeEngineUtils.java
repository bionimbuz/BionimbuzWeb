package app.common;

import static com.google.common.util.concurrent.MoreExecutors.newDirectExecutorService;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Properties;

import org.jclouds.ContextBuilder;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.concurrent.config.ExecutorServiceModule;
import org.jclouds.domain.Credentials;
import org.jclouds.googlecloud.GoogleCredentialsFromJson;
import org.jclouds.googlecomputeengine.GoogleComputeEngineApi;
import org.jclouds.googlecomputeengine.GoogleComputeEngineApiMetadata;
import org.jclouds.googlecomputeengine.domain.Operation;
import org.jclouds.googlecomputeengine.features.NetworkApi;
import org.jclouds.googlecomputeengine.features.OperationApi;
import org.jclouds.oauth.v2.AuthorizationApi;
import org.jclouds.oauth.v2.config.CredentialType;
import org.jclouds.oauth.v2.config.OAuthModule;
import org.jclouds.oauth.v2.config.OAuthProperties;
import org.jclouds.oauth.v2.config.OAuthScopes;
import org.jclouds.oauth.v2.config.OAuthScopes.SingleScope;
import org.jclouds.oauth.v2.domain.Claims;
import org.jclouds.oauth.v2.domain.Token;
import org.jclouds.rest.AnonymousHttpApiMetadata;

import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Binder;
import com.google.inject.Module;

public class GoogleComputeEngineUtils {    
    
    private static final String READ_SCOPE = "https://www.googleapis.com/auth/compute.readonly"; 
    private static final String WRITE_SCOPE = "https://www.googleapis.com/auth/compute"; 
    
    public static GoogleComputeEngineApi createApi(final String credential) {
        try {                        
            Supplier<Credentials> credentialSupplier = 
                    new GoogleCredentialsFromJson(credential);
            Credentials credentials = credentialSupplier.get();            
            Token token = getToken(credentials);              
            return createApiFromToken(credentials.identity, token.accessToken());            
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
       
    private static Token getToken(Credentials credentials) {
        
        try {
            int now = (int)ZonedDateTime.now(ZoneOffset.UTC).toEpochSecond();
            
            Claims claims = Claims.create( //
                    credentials.identity, // iss
                    WRITE_SCOPE, // scope
                    "https://accounts.google.com/o/oauth2/token", // aud
                    now + 60*60, // placeholder exp for the cache
                    now // placeholder iat for the cache
                );
            
            AuthorizationApi api = api(
                                    credentials, 
                                    new URL("https://accounts.google.com/o/oauth2/v2/auth"));

            Token token = api.authorize(claims);
            return token;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    private static AuthorizationApi api(Credentials credentials, URL url)
            throws IOException {

        return ContextBuilder
                .newBuilder(
                        AnonymousHttpApiMetadata.forApi(AuthorizationApi.class))
                .credentials(credentials.identity, credentials.credential)
                .endpoint(url.toString())
                .overrides(GoogleComputeEngineApiMetadata.defaultProperties())
                .modules(ImmutableSet.of(
                        new ExecutorServiceModule(newDirectExecutorService()),
                        new OAuthModule(), new Module() {
                            @Override
                            public void configure(Binder binder) {
                                binder.bind(OAuthScopes.class).toInstance(
                                        SingleScope.create(WRITE_SCOPE));
                            }
                        }))
                .buildApi(AuthorizationApi.class);
    }
    
    public static GoogleComputeEngineApi createApiFromToken(final String identity, final String token) {
        try {            
            Properties overrides = new Properties();            
            overrides.put(OAuthProperties.CREDENTIAL_TYPE,
                    CredentialType.BEARER_TOKEN_CREDENTIALS.toString());

            ComputeServiceContext context = ContextBuilder
                    .newBuilder(SystemConstants.CLOUD_TYPE).overrides(overrides)
                    .credentials(identity, token)
                    .buildView(ComputeServiceContext.class);

            GoogleComputeEngineApi googleApi = context
                    .unwrapApi(GoogleComputeEngineApi.class);
            return googleApi;

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
