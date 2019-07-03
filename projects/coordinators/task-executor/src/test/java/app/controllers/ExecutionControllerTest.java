package app.controllers;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;

import app.client.ExecutionApi;
import app.controllers.mocks.CoordinatorAccessControllerMock;
import app.models.Body;
import app.models.Command;
import app.models.SecureCoordinatorAccess;
import utils.TestUtils;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class ExecutionControllerTest {

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

        String token = CoordinatorAccessControllerMock.generateToken("1@machine", 2*1000l);        
        String baseUrl = TestUtils.getUrl(PORT);   
        SecureCoordinatorAccess secureFileAccess = 
                TestUtils.generateSecureFileAccess(baseUrl, token);   
        Command command = 
                TestUtils.generateCommand(baseUrl, secureFileAccess);  
        
        Body<Boolean> body = api.startExecution(command);
	    assertThat(body).isNotNull();
	    assertThat(body.getContent()).isTrue();
	    
        body = api.startExecution(command);
        assertThat(body).isNotNull();
        assertThat(body.getContent()).isFalse();
	}
}
