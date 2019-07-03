package app.execution.jobs;
import static app.common.SystemConstants.INPUTS_FOLDER;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;

import app.common.Pair;
import app.common.utils.FileUtils;
import app.controllers.mocks.CoordinatorAccessControllerMock;
import app.exceptions.SingletonAlreadyInitializedException;
import app.execution.CoordinatorServerAccess;
import app.execution.IApplicationExecution;
import app.models.ExecutionStatus.EXECUTION_PHASE;
import app.models.RemoteFileProcessingStatus;
import app.models.SecureCoordinatorAccess;
import utils.TestUtils;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class DownloaderJobTest implements IApplicationExecution{

    @Value("${local.server.port}")
    private int PORT;
    
    @Before
    public void init() {
        File file = new File(INPUTS_FOLDER);
        FileUtils.deleteDir(file);
    }

    @Test
    public void test() throws SingletonAlreadyInitializedException, InterruptedException {

        assertThat((new File(INPUTS_FOLDER).exists())).isFalse();
        
        String token = CoordinatorAccessControllerMock.generateToken("1@machine", 2*1000l);        
        String baseUrl = TestUtils.getUrl(PORT);        
        SecureCoordinatorAccess secureFileAccess = 
                TestUtils.generateSecureFileAccess(baseUrl, token);        
        CoordinatorServerAccess.init(
                baseUrl + CoordinatorAccessControllerMock.webRefreshStatusUrl,
                secureFileAccess);

        List<Pair<String, String>> inputs = TestUtils.generateInputs(baseUrl);        
        
        RemoteFileProcessingStatus status = 
                new RemoteFileProcessingStatus(inputs.size());
        DownloaderJob job = new DownloaderJob(
                this,
                status,
                inputs,
                INPUTS_FOLDER);
        
        job.start();
        
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

