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

import app.common.Pair;
import app.common.utils.FileUtils;
import app.controllers.mocks.FileInfoControllerMock;
import app.exceptions.SingletonAlreadyInitializedException;
import app.execution.jobs.DownloaderJob;
import app.models.ExecutionStatus.EXECUTION_PHASE;
import app.models.RemoteFileProcessingStatus;
import app.models.SecureFileAccess;
import utils.TestUtils;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class DownloaderJobTest implements IApplicationExecution{

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
    public void test() throws SingletonAlreadyInitializedException, InterruptedException {

        assertThat((new File(INPUTS_FOLDER).exists())).isFalse();
        
        String token = FileInfoControllerMock.generateToken("1@machine", 2*1000l);        
        String baseUrl = TestUtils.getUrl(PORT);        
        SecureFileAccess secureFileAccess = 
                TestUtils.generateSecureFileAccess(baseUrl, token);        
        RemoteFileInfoAccess.init(secureFileAccess);

        List<Pair<String, String>> inputs = new ArrayList<>();
        inputs.add(new Pair(baseUrl + FileInfoControllerMock.webDownloadUrl.replace("{id}", "1"), "txt"));
        inputs.add(new Pair(baseUrl + FileInfoControllerMock.webDownloadUrl.replace("{id}", "2"), "txt"));        
        
        RemoteFileProcessingStatus status = 
                new RemoteFileProcessingStatus(inputs.size());
        DownloaderJob downloader = new DownloaderJob(
                this,
                status,
                inputs,
                INPUTS_FOLDER);
        
        downloader.start();
        
        // Wait for downloads
        Thread.sleep(10*1000);
        
        assertThat((new File(INPUTS_FOLDER, "i0.txt").exists())).isTrue();
        assertThat((new File(INPUTS_FOLDER, "i1.txt").exists())).isTrue();
    }
    
    @Override
    public void onError(EXECUTION_PHASE phase, String message) {
        System.out.println("#### Error: " + message);
        assertThat(message).isNull();
    }

    @Override
    public void onSuccess(EXECUTION_PHASE phase) {
    }    
}

