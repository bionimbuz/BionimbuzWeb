package app.execution;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import app.controllers.mocks.FileInfoControllerMock;
import app.exceptions.SingletonAlreadyInitializedException;
import app.exceptions.SingletonNotInitializedException;
import app.models.RemoteFileInfo;
import app.models.SecureFileAccess;
import io.jsonwebtoken.ExpiredJwtException;
import utils.TestUtils;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class RemoteFileInfoAccessTest {

    @Value("${local.server.port}")
    private int PORT;
    @Autowired
    private FileInfoControllerMock controller;
    @Autowired
    private TestRestTemplate restTemplate;
    
    @Test
    // For this test, plugin-local must be running
    public void testSecurity() throws SingletonAlreadyInitializedException, SingletonNotInitializedException, InterruptedException {
        String token = FileInfoControllerMock.generateToken("1@machine", 2*1000l);        
        String baseUrl = TestUtils.getUrl(PORT);
        RemoteFileInfo remoteFileInfo = null;
        
        // Wait for token expiration
        Thread.sleep(3*0000);
        try {
            FileInfoControllerMock.checkToken(token);
        } catch(Exception e) {
            assertThat(e).isInstanceOf(ExpiredJwtException.class);
        }
        
        SecureFileAccess secureFileAccess = new SecureFileAccess(
                token,
                baseUrl + FileInfoControllerMock.webRefreshTokenUrl);
        
        // Call with a expired token
        RemoteFileInfoAccess.init(secureFileAccess);
        remoteFileInfo = 
                RemoteFileInfoAccess.get().getRemoteFileInfo(
                        baseUrl + FileInfoControllerMock.webDownloadUrl.replace("{id}", "1"));         

        assertThat(remoteFileInfo).isNotNull();
        
        secureFileAccess.setToken("invalid token");
        // Call with an invalid token
        remoteFileInfo = 
                RemoteFileInfoAccess.get().getRemoteFileInfo(
                        baseUrl + FileInfoControllerMock.webDownloadUrl.replace("{id}", "1"));         

        assertThat(remoteFileInfo).isNull();
    }
}

