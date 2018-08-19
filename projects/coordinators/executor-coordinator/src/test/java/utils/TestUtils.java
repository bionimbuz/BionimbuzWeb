package utils;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import app.common.Pair;
import app.controllers.mocks.FileInfoControllerMock;
import app.models.Command;
import app.models.SecureFileAccess;

public class TestUtils {
    protected static final Logger LOGGER = LoggerFactory.getLogger(TestUtils.class);

    private static final String PLUGIN_LOCAL_DIR = "../../plugins/plugin-local/";
    private static final String INSTANCES_DIR = PLUGIN_LOCAL_DIR + "instances/";
    private static final String EXECUTION_DIR = INSTANCES_DIR + "bionimbuz-instance-0/";
    public static final String URL_PLUGIN_LOCAL = "http://localhost:8282";
    
    public static String getUrl(int port) {
        return "http://localhost:"+port;
    }
    
    public static SecureFileAccess generateSecureFileAccess(
            final String baseUrl,
            final String token) {
        SecureFileAccess secureFileAccess = new SecureFileAccess(
                token,
                baseUrl + FileInfoControllerMock.webRefreshTokenUrl);
        return secureFileAccess;
    }    
    
    public static Command generateCommand(
            final String baseUrl,
            SecureFileAccess secureFileAccess) {

        List<Pair<String, String>> inputs = new ArrayList<>();
        inputs.add(new Pair(baseUrl + FileInfoControllerMock.webDownloadUrl.replace("{id}", "1"), "txt"));
        inputs.add(new Pair(baseUrl + FileInfoControllerMock.webDownloadUrl.replace("{id}", "2"), "txt"));
        
        List<Pair<String, String>> outputs = new ArrayList<>();
        inputs.add(new Pair(baseUrl + FileInfoControllerMock.webUploadUrl.replace("{id}", "3"), "txt"));
        
        Command command = new Command();
        command.setWorkinDir(".");
        command.setCommandLine("test_script.sh {a} {i:1} {i:2} {o:1}");
        command.setListInputPathsWithExtension(inputs);
        command.setListOutputPathsWithExtension(outputs);
        command.setArgs("-a -b -c content");
        command.setSecureFileAccess(secureFileAccess);
        return command;
    }    
    
    
}
