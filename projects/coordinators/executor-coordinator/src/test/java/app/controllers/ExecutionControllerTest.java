package app.controllers;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
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
import app.models.RemoteFileInfo;
import utils.TestUtils;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class ExecutionControllerTest {

    private static final String PLUGIN_LOCAL_DIR = "../../plugins/plugin-local/";
    private static final String SPACES_DIR = PLUGIN_LOCAL_DIR + "spaces/";
    private static final String INSTANCES_DIR = PLUGIN_LOCAL_DIR + "instances/";

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

	    List<RemoteFileInfo> listInputFiles =
	            new ArrayList<>();
        listInputFiles.add(
                createRemoteFileInfo("GET", "test", "test_input1.txt"));
        listInputFiles.add(
                createRemoteFileInfo("GET", "test", "test_input2.txt"));

        List<String> listOutputFiles = new ArrayList<>();
        listOutputFiles.add("test_output.txt");

	    Command command = new Command();
	    command.setWorkinDir(INSTANCES_DIR + "bionimbuz-instance-0/");
	    command.setCommandLine("test_script.sh {i:1} {i:2} {o:1}");
        command.setListInputs(listInputFiles);
        command.setListOutputs(listOutputFiles);
        command.setArgs("-a -b -c content");

        Body<Boolean> body = api.postCommand(command);

	    assertThat(body).isNotNull();
	    assertThat(body.getContent()).isTrue();
	}

    private RemoteFileInfo createRemoteFileInfo(
            final String method,
            final String space,
            final String fileName) {
        RemoteFileInfo inputFile;
        inputFile = new RemoteFileInfo();
	    inputFile.setMethod(method);
	    inputFile.setName(fileName);
	    inputFile.setHeaders(new HashMap<String, String>());
	    inputFile.setUrl("http://localhost:8282/spaces/"+space+"/file/"+fileName+"/download");
	    return inputFile;
    }
}
