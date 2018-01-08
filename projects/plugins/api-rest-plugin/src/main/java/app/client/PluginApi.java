package app.client;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.jclouds.domain.Credentials;
import org.jclouds.googlecloud.GoogleCredentialsFromJson;
import org.jclouds.googlecomputeengine.GoogleComputeEngineApiMetadata;
import org.jclouds.oauth.v2.AuthorizationApi;
import org.jclouds.oauth.v2.config.OAuthProperties;
import org.jclouds.oauth.v2.domain.Claims;
import org.jclouds.oauth.v2.domain.Token;

import com.google.common.base.Supplier;

import app.common.Authorization;
import app.common.GlobalConstants;
import app.models.security.TokenModel;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class PluginApi {
    
    private static final int CONNECT_TIMEOUT_SECS = 10;
    private static final int READ_TIMEOUT_SECS = 10;
    
    public static final String CLOUD_TYPE_GCE = "google-compute-engine";
    
    private OkHttpClient client;    
    private Retrofit retrofit;  
    
    public <T> T createApi(Class<T> clazz) {
        return retrofit.create(clazz);
    }      
    
    public PluginApi(final String url) {        
        this.client = new OkHttpClient.Builder()
                .connectTimeout(CONNECT_TIMEOUT_SECS, TimeUnit.SECONDS)
                .readTimeout(READ_TIMEOUT_SECS,TimeUnit.SECONDS).build();
        this.retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(JacksonConverterFactory.create())
                .client(client)
                .build();
    }    
    
    private Supplier<Credentials> getCredentialSuplier( 
            final String cloudType,
            final String credentialContent) {
        if(cloudType == CLOUD_TYPE_GCE) {     
            return new GoogleCredentialsFromJson(credentialContent);            
        }        
        return null;
    }
    
    private Properties getProperties(
            final String cloudType) {
        if(cloudType == CLOUD_TYPE_GCE) {     
            return GoogleComputeEngineApiMetadata.defaultProperties();            
        }        
        return null;
    }
    
    public TokenModel getToken(
            final String cloudType,
            final String scope,
            final String credentialContent
            ) throws Exception {
        
        Properties properties = getProperties(cloudType);
        Supplier<Credentials> credentialSuplier = 
                getCredentialSuplier(cloudType, credentialContent);
        
        final String aud = properties.getProperty(OAuthProperties.AUDIENCE);
        int now = (int)ZonedDateTime.now(ZoneOffset.UTC).toEpochSecond();        

        Claims claims = Claims.create( //
                credentialSuplier.get().identity, // iss 
                scope, // scope
                aud, // aud
                now + GlobalConstants.TOKEN_LIFETIME_SECONDS, // placeholder exp for the cache
                now // placeholder iat for the cache
            );
        
        try(AuthorizationApi api = 
                Authorization.createApi(
                        credentialSuplier, cloudType)) {        
            Token token = api.authorize(claims);            
            return new TokenModel(
                    token.accessToken(), 
                    credentialSuplier.get().identity);
        }
    }    
}
