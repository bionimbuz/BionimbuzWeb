package utils;

import app.common.Authorization;
import app.common.GlobalConstants;
import app.common.HttpHeadersCustom;
import app.common.SystemConstants;
import app.models.security.TokenModel;
import com.google.common.io.Files;
import org.openstack4j.api.OSClient;
import org.openstack4j.openstack.OSFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import static app.common.OSClientHelper.getOSClient;
import static org.assertj.core.api.Assertions.assertThat;

public class TestUtils {
    private static String readCredential() {
        String fileContents = null;
        try {
            fileContents =
                    Files.toString(
                            new File(CmdLineArgs.getCredentialFile()),
                            Charset.defaultCharset());
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertThat(fileContents).isNotNull();
        return fileContents;
    }


    public static <T> HttpEntity<T> createEntity(T content) {
        OSFactory.enableHttpLoggingFilter(true);
        String credential = readCredential();

        try {
            TokenModel tokenModel = Authorization.getToken(SystemConstants.CLOUD_TYPE, null, credential);
            OSClient.OSClientV3 os = getOSClient(tokenModel.getToken());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.add(HttpHeadersCustom.AUTHORIZATION_ID, os.getToken().getId());
            headers.add(HttpHeaders.AUTHORIZATION, os.getToken().getId());
            headers.add(HttpHeadersCustom.API_VERSION, GlobalConstants.API_VERSION);

            HttpEntity<T> entity =
                    new HttpEntity<>(content, headers);

            return entity;
        } catch (Exception e) {
            e.printStackTrace();
            assertThat(e).isNull();
            return null;
        }
    }

    public static void setTimeout(RestTemplate restTemplate, int timeout) {
        restTemplate.setRequestFactory(new SimpleClientHttpRequestFactory());
        SimpleClientHttpRequestFactory rf =
                (SimpleClientHttpRequestFactory) restTemplate.getRequestFactory();
        rf.setReadTimeout(timeout);
        rf.setConnectTimeout(timeout);
    }

}
