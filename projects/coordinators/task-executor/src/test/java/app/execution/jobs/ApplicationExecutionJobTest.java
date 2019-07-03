package app.execution.jobs;
import static app.common.SystemConstants.INPUTS_FOLDER;
import static app.common.SystemConstants.OUTPUTS_FOLDER;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;

import app.common.utils.FileUtils;
import app.controllers.mocks.CoordinatorAccessControllerMock;
import app.exceptions.SingletonAlreadyInitializedException;
import app.exceptions.SingletonNotInitializedException;
import app.execution.CoordinatorServerAccess;
import app.models.Command;
import app.models.ExecutionStatus;
import app.models.ExecutionStatus.EXECUTION_PHASE;
import app.models.ExecutionStatus.STATUS;
import app.models.SecureCoordinatorAccess;
import utils.TestUtils;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class ApplicationExecutionJobTest implements IStatusRefresh{

    private static final String PLUGIN_LOCAL_DIR = "../../plugins/plugin-local/";
    private static final String SPACES_DIR = PLUGIN_LOCAL_DIR + "spaces/";
    private static final String TEST_SPACE = SPACES_DIR + "test/";
    private static final String outputFile0 = CoordinatorAccessControllerMock.getFileNameById(3);
    private static final String outputFile1 = CoordinatorAccessControllerMock.getFileNameById(4);
    
    @Value("${local.server.port}")
    private int PORT;
    @Autowired
    private CoordinatorAccessControllerMock controller;
    
    @Before
    public void init() {
        File file = new File(INPUTS_FOLDER);
        FileUtils.deleteDir(file);
        file = new File(OUTPUTS_FOLDER);
        FileUtils.deleteDir(file);
        
        file = new File(TEST_SPACE, outputFile0);
        if(file.exists()) {
            file.delete();
        }
        file = new File(TEST_SPACE, outputFile1);
        if(file.exists()) {
            file.delete();
        }
    }

    @Test
    public void test() throws SingletonAlreadyInitializedException, InterruptedException, SingletonNotInitializedException {

        assertThat((new File(INPUTS_FOLDER).exists())).isFalse();
        assertThat((new File(OUTPUTS_FOLDER).exists())).isFalse();
        assertThat((new File(TEST_SPACE, outputFile0).exists())).isFalse();
        assertThat((new File(TEST_SPACE, outputFile1).exists())).isFalse();
        
        String token = CoordinatorAccessControllerMock.generateToken("1@machine", 2*1000l); 
        controller.setStatusRefresh(this);
        String baseUrl = TestUtils.getUrl(PORT);   
        
        SecureCoordinatorAccess secureFileAccess = 
                TestUtils.generateSecureFileAccess(baseUrl, token);   
        Command command = 
                TestUtils.generateCommand(baseUrl, secureFileAccess);     
        
        CoordinatorServerAccess.init(
                command.getRefreshStatusUrl(), 
                command.getSecureFileAccess());
        ApplicationExecutionJob.init(command);
        
        // Wait for complete execution        
        Thread.sleep(10*1000);
        
        ExecutionStatus status = 
                ApplicationExecutionJob.get().getExecutionStatus();     

        assertThat(status.getPhase()).isEqualTo(EXECUTION_PHASE.FINISHED);
        
        assertThat((new File(INPUTS_FOLDER, "i0.txt").exists())).isTrue();
        assertThat((new File(INPUTS_FOLDER, "i1.txt").exists())).isTrue();
        assertThat((new File(OUTPUTS_FOLDER, "o0.txt").exists())).isTrue();
        assertThat((new File(OUTPUTS_FOLDER, "o1.txt").exists())).isTrue();
        assertThat((new File(TEST_SPACE, outputFile0).exists())).isTrue();
        assertThat((new File(TEST_SPACE, outputFile1).exists())).isTrue();
    }

    @Override
    public void onStatusRefreshed(STATUS status, EXECUTION_PHASE phase, String errorMessage) {
        System.out.println("Status: " + status + " | Phase: " + phase + " | Error: " + errorMessage);        
    }    
}

