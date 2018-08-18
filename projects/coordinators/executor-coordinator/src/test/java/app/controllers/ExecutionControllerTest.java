package app.controllers;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;

import app.client.ExecutionApi;
import app.models.Body;
import app.models.Command;
import utils.TestUtils;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class ExecutionControllerTest {

    private static final String PLUGIN_LOCAL_DIR = "../../plugins/plugin-local/";
    private static final String SPACES_DIR = PLUGIN_LOCAL_DIR + "spaces/";
    private static final String INSTANCES_DIR = PLUGIN_LOCAL_DIR + "instances/";
    private static final String EXECUTION_DIR = INSTANCES_DIR + "bionimbuz-instance-0/";

    @Autowired
    private ExecutionController controller;
    @Value("${local.server.port}")
    private int PORT;

    @Test
    public void contexLoads() throws Exception {
        assertThat(controller).isNotNull();
    }

	@Test
	public void startExecutionTest() throws IOException {

        ExecutionApi api = new ExecutionApi(TestUtils.getUrl(PORT));

	    List<String> listInputFiles =
	            new ArrayList<>();
        listInputFiles.add(
                "http://localhost:8282/spaces/"+"test"+"/file/"+"test_input1.txt"+"/download");
        listInputFiles.add(
                "http://localhost:8282/spaces/"+"test"+"/file/"+"test_input2.txt"+"/download");

        List<String> listOutputFiles = new ArrayList<>();
        listOutputFiles.add(
                "http://localhost:8282/spaces/"+"test"+"/file/"+"test_output.txt"+"/upload");

	    Command command = new Command();
	    command.setWorkinDir(EXECUTION_DIR);
	    command.setCommandLine("test_script.sh {a} {i:1} {i:2} {o:1}");
        command.setListRemoteFileInputPaths(listInputFiles);
        command.setListRemoteFileOutputPaths(listOutputFiles);
        command.setArgs("-a -b -c content");

        Body<Boolean> body = api.startExecution(command);
	    assertThat(body).isNotNull();
	    assertThat(body.getContent()).isTrue();
	    
        body = api.startExecution(command);
        assertThat(body).isNotNull();
        assertThat(body.getContent()).isFalse();
	}

    private String createRemoteFileInfoUrl(
            final String space,
            final String fileName) {
	    return "http://localhost:8282/spaces/"+space+"/file/"+fileName+"/download";
    }
}
