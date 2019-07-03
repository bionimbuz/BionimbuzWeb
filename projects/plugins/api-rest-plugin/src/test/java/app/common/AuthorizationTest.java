package app.common;

import static org.assertj.core.api.Assertions.assertThat;

import org.jclouds.ContextBuilder;
import org.jclouds.domain.Credentials;
import org.jclouds.ec2.EC2ApiMetadata;
import org.jclouds.oauth.v2.AuthorizationApi;
import org.junit.Test;

import com.google.common.base.Supplier;

import app.utils.TestUtils;

public class AuthorizationTest extends Authorization {

    @Test
    public void createApiTest() throws Exception {


        {
        String credentialContent =
                TestUtils.readFileContent("../../web/conf/credentials/credentials-gcp.json");
        Supplier<Credentials> supplierAws =
                getCredentialSuplier(
                        CLOUD_TYPE_GCE,
                        credentialContent);
        AuthorizationApi api =
                createApi(supplierAws, CLOUD_TYPE_GCE);
        assertThat(api).isNotNull();
        }


        {
        String credentialContent =
                TestUtils.readFileContent("../../web/conf/credentials/credential_aws2.csv");
        Supplier<Credentials> supplierAws =
                getCredentialSuplier(
                        CLOUD_TYPE_AWS_EC2,
                        credentialContent);

        AuthorizationApi oauth =
                ContextBuilder
                    .newBuilder(new EC2ApiMetadata())
                        .credentialsSupplier(supplierAws)
                        .buildApi(AuthorizationApi.class);
        assertThat(oauth).isNotNull();
        }
    }

//
//
//
//
//    public static AuthorizationApi createApi(
//            final Supplier<Credentials> credentials,
//            final String cloudType) throws Exception {
//        AuthorizationApi oauth =
//                ContextBuilder
//                    .newBuilder(cloudType)
//                        .credentialsSupplier(credentials)
//                        .buildApi(AuthorizationApi.class);
//        return oauth;
//    }


}
