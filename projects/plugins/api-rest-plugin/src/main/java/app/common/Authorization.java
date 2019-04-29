package app.common;

import app.common.supliers.AWSAccessKeyFromContent;
import app.models.security.TokenModel;
import com.google.common.base.Supplier;
import org.jclouds.ContextBuilder;
import org.jclouds.aws.ec2.AWSEC2ProviderMetadata;
import org.jclouds.domain.Credentials;
import org.jclouds.googlecloud.GoogleCredentialsFromJson;
import org.jclouds.googlecomputeengine.GoogleComputeEngineApiMetadata;
import org.jclouds.oauth.v2.AuthorizationApi;
import org.jclouds.oauth.v2.config.OAuthProperties;
import org.jclouds.oauth.v2.domain.Claims;
import org.jclouds.oauth.v2.domain.Token;
import org.openstack4j.api.OSClient;
import org.openstack4j.core.transport.Config;
import org.openstack4j.model.common.Identifier;
import org.openstack4j.openstack.OSFactory;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Properties;

import static app.common.GlobalConstants.*;

public class Authorization {

    public static final String CLOUD_TYPE_GCE = "google-compute-engine";
    public static final String CLOUD_TYPE_AWS_EC2 = "aws-ec2";
    public static final String CLOUD_TYPE_LOCAL = "local-machine";
    public static final String CLOUD_TYPE_OPENSTACK = "openstack";

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

    protected static Supplier<Credentials> getCredentialSuplier(
            final String cloudType,
            final String credentialContent) throws Exception {
        // TODO: to remove this dependency
        if(cloudType.equals(CLOUD_TYPE_GCE)) {
            return new GoogleCredentialsFromJson(credentialContent);
        } else if(cloudType.equals(CLOUD_TYPE_AWS_EC2)) {
            return new AWSAccessKeyFromContent(credentialContent);
        }
        return null;
    }

    protected static Properties getProperties(
            final String cloudType) {
        // TODO: to remove this dependency
        if(cloudType.equals(CLOUD_TYPE_GCE)) {
            return GoogleComputeEngineApiMetadata.defaultProperties();
        } else if(cloudType.equals(CLOUD_TYPE_AWS_EC2)) {
            return AWSEC2ProviderMetadata.defaultProperties();
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

        if (cloudType.equals(CLOUD_TYPE_OPENSTACK)) {
            OSClient.OSClientV3 os = OSFactory.builderV3()
                    .endpoint(KEYSTONE_HOST)
                    .withConfig(Config.newConfig().withEndpointNATResolution(HOST))
                    .credentials(TEST_PROJECT_USER, TEST_PROJECT_PASS, Identifier.byName(TEST_PROJECT_DOMAIN))
                    .scopeToProject(Identifier.byId(TEST_PROJECT_ID))
                    .authenticate();
            TokenModel tokenModel = new TokenModel(null, null);
            tokenModel.setOs4jToken(os.getToken());
            return tokenModel;
        }

        Properties properties = getProperties(cloudType);
        Supplier<Credentials> credentialSuplier =
                getCredentialSuplier(cloudType, credentialContent);
        
        if(cloudType.equals(CLOUD_TYPE_AWS_EC2)) {
            return new TokenModel(
                    credentialSuplier.get().credential,
                    credentialSuplier.get().identity);
        }

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
