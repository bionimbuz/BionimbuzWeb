package app.common.supliers;

import static org.assertj.core.api.Assertions.assertThat;

import org.jclouds.domain.Credentials;
import org.junit.Test;

import app.utils.TestUtils;

public class AWSAccessKeyFromContentTest{

    private String [] credential_files = {
            "resources/credential_aws_format1.csv",
            "resources/credential_aws_format2.csv",
            "resources/credential_aws_format3.csv"};

    @Test
    public void testFormats() throws Exception {

        for(String credentialPath : credential_files) {
            String fileContent =
                    TestUtils.readFileContent(credentialPath);

            AWSAccessKeyFromContent awsKey =
                    new AWSAccessKeyFromContent(fileContent);
            Credentials credential =
                    awsKey.get();

            assertThat(credential.identity).isNotEmpty();
            assertThat(credential.credential).isNotEmpty();
        }
    }
}
