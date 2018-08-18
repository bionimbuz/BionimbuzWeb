package app.execution;

import static app.common.SystemConstants.INPUTS_FOLDER;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;

import app.common.utils.FileUtils;
import app.controllers.mocks.FileInfoControllerMock;
import app.exceptions.SingletonAlreadyInitializedException;
import app.models.ExecutionStatus.EXECUTION_PHASE;
import app.models.RemoteFileProcessingStatus;
import app.models.SecureFileAccess;
import utils.TestUtils;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class DownloaderJobTest implements IExecution{

    @Value("${local.server.port}")
    private int PORT;
    @Autowired
    private FileInfoControllerMock controller;
    
    @Before
    public void init() {
        File file = new File(INPUTS_FOLDER);
        FileUtils.deleteDir(file);
    }

    @Test
    public void testDownloader() throws SingletonAlreadyInitializedException, InterruptedException {
        
        String token = FileInfoControllerMock.generateToken("1@machine", 2*1000l);        
        String baseUrl = TestUtils.getUrl(PORT);        
        SecureFileAccess secureFileAccess = new SecureFileAccess(
                token,
                baseUrl + FileInfoControllerMock.webRefreshTokenUrl);        
        RemoteFileInfoAccess.init(secureFileAccess);
        
        List<String> inputs = new ArrayList<>();
        inputs.add(baseUrl + FileInfoControllerMock.webDownloadUrl.replace("{id}", "1"));
        inputs.add(baseUrl + FileInfoControllerMock.webDownloadUrl.replace("{id}", "2"));
        
        RemoteFileProcessingStatus status = 
                new RemoteFileProcessingStatus(inputs.size());
        DownloaderJob downloader = new DownloaderJob(
                this,
                status,
                inputs,
                INPUTS_FOLDER);
        
        downloader.start();
        
        // Wait for downloads
        Thread.sleep(300*1000);
    }    
    
    @Override
    public void onError(EXECUTION_PHASE phase, String message) {
        assertThat(message).isNull();
    }

    @Override
    public void onSuccess(EXECUTION_PHASE phase) {
    }    
}

