package app.common;

import org.jclouds.ContextBuilder;
import org.jclouds.domain.Credentials;
import org.jclouds.oauth.v2.AuthorizationApi;

import com.google.common.base.Supplier;

public class Authorization {
    
    public static AuthorizationApi createApi(
            final Supplier<Credentials> credentials, 
            final String cloudType) throws Exception {        
        AuthorizationApi oauth = 
                ContextBuilder
                    .newBuilder(cloudType)
                        .credentialsSupplier(credentials)
                        .buildApi(AuthorizationApi.class);        
        return oauth;
    }

}
