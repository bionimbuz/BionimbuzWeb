package utils;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
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

    private static final String getAbsoluteCurrentDir() {
        return System.getProperty("user.dir") + "/";        
    }
    
    public static Command generateCommand(
            final String baseUrl,
            SecureFileAccess secureFileAccess) {

        List<Pair<String, String>> inputs = generateInputs(baseUrl);        
        List<Pair<String, String>> outputs = generateOutputs(baseUrl);
        
        Command command = new Command();
        command.setCommandLine("{i} {i} {i} {o} {o} {o}");
        command.setExecutionScript(
                "#!/bin/bash\n" + 
                "\n" + 
                "cat $1 > $3\n" + 
                "cat $2 >> $3\n" + 
                "\n" + 
                "echo \"Execution time: `date`\" >> $3\n" + 
                "\n" + 
                "echo \"Extra file execution time: `date`\" >> $4");
        command.setListInputPathsWithExtension(inputs);
        command.setListOutputPathsWithExtension(outputs);
        command.setArgs("-a -b -c content");
        command.setSecureFileAccess(secureFileAccess);
        return command;
    }

    public static List<Pair<String, String>> generateInputs(
            final String baseUrl) {
        List<Pair<String, String>> inputs = new ArrayList<>();
        inputs.add(new Pair<>(baseUrl + FileInfoControllerMock.webDownloadUrl.replace("{id}", "1"), "txt"));
        inputs.add(new Pair<>(baseUrl + FileInfoControllerMock.webDownloadUrl.replace("{id}", "2"), "txt"));
        return inputs;
    }    

    public static List<Pair<String, String>> generateOutputs(
            final String baseUrl) {
        List<Pair<String, String>> outputs = new ArrayList<>();
        outputs.add(new Pair<>(baseUrl + FileInfoControllerMock.webUploadUrl.replace("{id}", "3"), "txt"));
        outputs.add(new Pair<>(baseUrl + FileInfoControllerMock.webUploadUrl.replace("{id}", "4"), "txt"));
        return outputs;
    }
    
    public static void createFile(String fileName, String fileContent, String dir)  {
        File file = new File(dir, fileName);
        if(file.exists()) {
            file.delete();
        }
        try(PrintWriter writer = new PrintWriter(dir + fileName, "UTF-8")){
            writer.println(fileContent);
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            assertThat(e).isNull();
        }
    }
    
}
