package app.execution.jobs;
import static app.common.SystemConstants.OUTPUTS_FOLDER;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
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
import app.execution.IApplicationExecution;
import app.execution.RemoteFileInfoAccess;
import app.models.ExecutionStatus.EXECUTION_PHASE;
import app.models.RemoteFileProcessingStatus;
import app.models.SecureFileAccess;
import utils.TestUtils;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class UploaderJobTest implements IApplicationExecution{

    private static final String PLUGIN_LOCAL_DIR = "../../plugins/plugin-local/";
    private static final String SPACES_DIR = PLUGIN_LOCAL_DIR + "spaces/";
    private static final String TEST_SPACE = SPACES_DIR + "test/";
    private static final String outputFile0 = FileInfoControllerMock.getFileNameById(3);
    private static final String outputFile1 = FileInfoControllerMock.getFileNameById(4);
    
    @Value("${local.server.port}")
    private int PORT;
    @Autowired
    private FileInfoControllerMock controller;
    
    @Before
    public void init() {
        File dir = new File(OUTPUTS_FOLDER);
        FileUtils.deleteDir(dir);
        dir.mkdir();
        
        TestUtils.createFile("o0.txt", "output file 0", OUTPUTS_FOLDER);
        TestUtils.createFile("o1.txt", "output file 1", OUTPUTS_FOLDER);  
        
        File file = new File(TEST_SPACE, outputFile0);
        if(file.exists()) {
            file.delete();
        }
        file = new File(TEST_SPACE, outputFile1);
        if(file.exists()) {
            file.delete();
        }
    }

    @Test
    public void test() throws SingletonAlreadyInitializedException, InterruptedException {
        
        assertThat((new File(TEST_SPACE, outputFile0).exists())).isFalse();
        assertThat((new File(TEST_SPACE, outputFile1).exists())).isFalse();
        
        FileInfoControllerMock.getFileNameById(3);
        
        String token = FileInfoControllerMock.generateToken("1@machine", 2*1000l);        
        String baseUrl = TestUtils.getUrl(PORT);        
        SecureFileAccess secureFileAccess = 
                TestUtils.generateSecureFileAccess(baseUrl, token);        
        RemoteFileInfoAccess.init(secureFileAccess);

        List<Pair<String, String>> outputs = TestUtils.generateOutputs(baseUrl);        
        
        RemoteFileProcessingStatus status = 
                new RemoteFileProcessingStatus(outputs.size());
        UploaderJob job = new UploaderJob(
                this,
                status,
                outputs,
                OUTPUTS_FOLDER);
        
        job.start();
        
        // Wait for downloads
        Thread.sleep(10*1000);        

        assertThat((new File(TEST_SPACE, outputFile0).exists())).isTrue();
        assertThat((new File(TEST_SPACE, outputFile1).exists())).isTrue();
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

