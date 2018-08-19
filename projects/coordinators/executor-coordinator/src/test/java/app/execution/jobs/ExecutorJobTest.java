package app.execution.jobs;

import static app.common.SystemConstants.INPUTS_FOLDER;
import static app.common.SystemConstants.OUTPUTS_FOLDER;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;

import app.common.utils.FileUtils;
import app.exceptions.SingletonAlreadyInitializedException;
import app.execution.IApplicationExecution;
import app.models.Command;
import app.models.ExecutionStatus.EXECUTION_PHASE;
import utils.TestUtils;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class ExecutorJobTest implements IApplicationExecution{

    @Value("${local.server.port}")
    private int PORT;
    
    @Before
    public void init() {

        File dir = new File(INPUTS_FOLDER);
        dir.mkdir();
        
        dir = new File(OUTPUTS_FOLDER);
        FileUtils.deleteDir(dir);
        dir.mkdir();
        
        TestUtils.createInputFile("i0.txt", "input file 0");
        TestUtils.createInputFile("i1.txt", "input file 1");        
    }

    @Test
    public void test() throws SingletonAlreadyInitializedException, InterruptedException {     
        String baseUrl = TestUtils.getUrl(PORT);   
        Command command = 
                TestUtils.generateCommand(baseUrl, null);   

        ExecutorJob executor = new ExecutorJob(
                this,
                command);
        executor.start();
        
        Thread.sleep(10*1000);        
        
        assertThat((new File(OUTPUTS_FOLDER, "o0.txt").exists())).isTrue();
        assertThat((new File(OUTPUTS_FOLDER, "o1.txt").exists())).isTrue();
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

