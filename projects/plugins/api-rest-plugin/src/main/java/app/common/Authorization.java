package app.common;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Properties;

import org.jclouds.ContextBuilder;
import org.jclouds.domain.Credentials;
import org.jclouds.googlecloud.GoogleCredentialsFromJson;
import org.jclouds.googlecomputeengine.GoogleComputeEngineApiMetadata;
import org.jclouds.oauth.v2.AuthorizationApi;
import org.jclouds.oauth.v2.config.OAuthProperties;
import org.jclouds.oauth.v2.domain.Claims;
import org.jclouds.oauth.v2.domain.Token;

import com.google.common.base.Supplier;

import app.models.security.TokenModel;

public class Authorization {

    public static final String CLOUD_TYPE_GCE = "google-compute-engine";
    public static final String CLOUD_TYPE_LOCAL = "local-machine";

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

    private static Supplier<Credentials> getCredentialSuplier(
            final String cloudType,
            final String credentialContent) {
        // TODO: to remove this dependency
        if(cloudType == CLOUD_TYPE_GCE) {
            return new GoogleCredentialsFromJson(credentialContent);
        }
        return null;
    }

    private static Properties getProperties(
            final String cloudType) {
        // TODO: to remove this dependency
        if(cloudType == CLOUD_TYPE_GCE) {
            return GoogleComputeEngineApiMetadata.defaultProperties();
        }
        return null;
    }

    public static TokenModel getToken(
            final String cloudType,
            final String scope,
            final String credentialContent
            ) throws Exception {

        if(cloudType.equals(CLOUD_TYPE_LOCAL)) {
            return new TokenModel("","");
        }

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
