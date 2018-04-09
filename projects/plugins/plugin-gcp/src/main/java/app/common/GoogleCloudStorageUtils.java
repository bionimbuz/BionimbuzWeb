package app.common;

import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.jclouds.ContextBuilder;
import org.jclouds.apis.ApiMetadata;
import org.jclouds.apis.Apis;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.googlecloudstorage.GoogleCloudStorageApi;
import org.jclouds.googlecomputeengine.GoogleComputeEngineApi;
import org.jclouds.googlecomputeengine.domain.Operation;
import org.jclouds.googlecomputeengine.features.OperationApi;
import org.jclouds.oauth.v2.config.CredentialType;
import org.jclouds.oauth.v2.config.OAuthProperties;
import org.jclouds.providers.ProviderMetadata;
import org.jclouds.providers.Providers;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;

public class GoogleCloudStorageUtils {    
    
    public static final Map<String, ApiMetadata> allApis = Maps.uniqueIndex(Apis.viewableAs(BlobStoreContext.class),
            Apis.idFunction());
       
    public static final Map<String, ProviderMetadata> appProviders = Maps.uniqueIndex(Providers.viewableAs(BlobStoreContext.class),
        Providers.idFunction());
   
    public static final Set<String> allKeys = ImmutableSet.copyOf(Iterables.concat(appProviders.keySet(), allApis.keySet()));
    
    public static GoogleCloudStorageApi createApi(final String identity, String token) throws Exception {   
        
        if(token.startsWith(HttpHeadersCustom.HEADER_VALUE_AUTHORIZATION_BEARER)) {
            token = token.replaceFirst(HttpHeadersCustom.HEADER_VALUE_AUTHORIZATION_BEARER, "");
        }
        
        Properties overrides = new Properties();
        overrides.put(OAuthProperties.CREDENTIAL_TYPE,
                CredentialType.BEARER_TOKEN_CREDENTIALS.toString());

        ContextBuilder builder = ContextBuilder
                .newBuilder(SystemConstants.CLOUD_STORAGE_TYPE);
        
        BlobStoreContext context = builder.overrides(overrides)
                .credentials(identity, token)
                .buildView(BlobStoreContext.class);

        GoogleCloudStorageApi googleApi = context
                .unwrapApi(GoogleCloudStorageApi.class);
        return googleApi;        
    }      
    
    
    
    
    
    
    public static GoogleCloudStorageApi createApi2(final String identity, String token) throws Exception {   
        
        if(token.startsWith(HttpHeadersCustom.HEADER_VALUE_AUTHORIZATION_BEARER)) {
            token = token.replaceFirst(HttpHeadersCustom.HEADER_VALUE_AUTHORIZATION_BEARER, "");
        }
        
        Properties overrides = new Properties();
        overrides.put(OAuthProperties.CREDENTIAL_TYPE,
                CredentialType.BEARER_TOKEN_CREDENTIALS.toString());

        ContextBuilder builder = ContextBuilder
                .newBuilder(SystemConstants.CLOUD_COMPUTE_TYPE);

        BlobStoreContext context = builder.overrides(overrides)
                .credentials(identity, token)
                .buildView(BlobStoreContext.class);

        GoogleCloudStorageApi googleApi = context
                .unwrapApi(GoogleCloudStorageApi.class);
        return googleApi;        
    }      
    
    public static void waitOperation(
            final GoogleComputeEngineApi googleApi, final Operation operation) {
        OperationApi operationsApi = googleApi.operations();
        ThreadHelper.waitForOperation(operationsApi, operation);
    }
}
