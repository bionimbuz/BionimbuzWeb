package app.controllers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import app.common.FileUtils;
import app.common.GlobalConstants;
import app.common.OsUtil;
import app.common.SystemConstants;
import app.models.Body;
import app.models.PluginInstanceModel;

@RestController
public class InstanceController extends AbstractInstanceController {

    private static final String STARTUP_SCRIPT_NAME = "startup_script";
    private static Integer instanceIdSequence = 0;
    private static Map<Integer, InstanceProcess> processes = new HashMap<>();

    /*
     * Overwritten Methods
     */

    @Override
    protected ResponseEntity<Body<List<PluginInstanceModel>>> createInstance(
            final String token,
            final String identity,
            final List<PluginInstanceModel> listModel) throws Exception {

        for (PluginInstanceModel pluginInstanceModel : listModel) {

            Integer newId = getNextInstanceId();
            String instanceName =
                    PluginInstanceModel.generateNameForId(
                            newId, GlobalConstants.BNZ_PREFIX);

            String ip = System.getProperty(
                    SystemConstants.SYSTEM_PROPERTY_IP,
                    InetAddress.getLocalHost().getHostAddress());

            pluginInstanceModel.setId(String.valueOf(newId));
            pluginInstanceModel.setName(instanceName);
            pluginInstanceModel.setExternalIp(ip);

            String instancePath =
                    createInstanceDir(instanceName);

            String startupScript = 
                    generateStartupScript(
                            instancePath,
                            pluginInstanceModel.getScriptExtension(),
                            pluginInstanceModel.getStartupScript());
            
            Process process = Runtime.getRuntime().exec(
                    startupScript,
                    null,
                    new File(instancePath));

            InstanceProcess instanceProcess =
                    new InstanceProcess(instancePath, process, pluginInstanceModel);

            insertInstanceProcess(newId, instanceProcess);
        }

        return ResponseEntity.ok(
                Body.create(listModel));
    }   
    
    protected String generateStartupScript(
            final String path, 
            String extension, 
            final String content) throws IOException { 
        if(extension == null || extension.trim().isEmpty())
            extension =  OsUtil.getDefaultScriptExtension();
        File scriptFile = new File(path, STARTUP_SCRIPT_NAME + "." + extension);
        String absolutePath = scriptFile.getAbsolutePath();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(scriptFile))) {      
            writer.write(content);     
        }
        return absolutePath;
    }

    @Override
    protected ResponseEntity<Body<PluginInstanceModel>> getInstance(
            final String token,
            final String identity,
            final String zone,
            final String name) throws Exception {
        if(!SystemConstants.PLUGIN_ZONE.equals(zone)) {
            return new ResponseEntity<>(
                    Body.create(null),
                    HttpStatus.NOT_FOUND);
        }

        Integer instanceId =
                PluginInstanceModel.extractIdFromName(
                        name, GlobalConstants.BNZ_PREFIX);

        InstanceProcess instanceProcess =
                getInstanceProcess(instanceId);

        if(instanceProcess == null) {
            return new ResponseEntity<>(
                    Body.create(null),
                    HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(
                Body.create(instanceProcess.getPluginInstance()));
    }

    @Override
    protected ResponseEntity<Body<Boolean>> deleteInstance(
            final String token,
            final String identity,
            final String zone,
            final String name) throws Exception {

        deleteInstanceDir(name);

        Integer instanceId =
                PluginInstanceModel.extractIdFromName(
                        name, GlobalConstants.BNZ_PREFIX);

        if(!existsInstanceProcess(instanceId)) {
            return new ResponseEntity<>(
                    Body.create(false),
                    HttpStatus.OK);
        }

        removeInstanceProcess(instanceId);

        return new ResponseEntity<>(
                Body.create(true),
                HttpStatus.OK);
    }

    @Override
    protected ResponseEntity<Body<List<PluginInstanceModel>>> listInstances(
            final String token,
            final String identity) throws Exception {

        List<PluginInstanceModel> res = new ArrayList<>();
        for (Map.Entry<Integer, InstanceProcess> entry : processes.entrySet()) {
            res.add(entry.getValue().getPluginInstance());
        }

        return ResponseEntity.ok(
                Body.create(res));
    }

    private static class InstanceProcess implements Runnable{

        private String workingDirPath;
        private Process process;
        private PluginInstanceModel pluginInstance;

        public InstanceProcess(
                String workingDirPath,
                Process process,
                PluginInstanceModel pluginInstance) {
            this.workingDirPath = workingDirPath;
            this.process = process;
            this.pluginInstance = pluginInstance;   
            startWritingStdoutFile();
        }
        
        private void startWritingStdoutFile() {
            new Thread(this).start();;
        }

        public Process getProcess() {
            return process;
        }
        public PluginInstanceModel getPluginInstance() {
            return pluginInstance;
        }

        @Override
        public void run() {
            File stdoutFile = new File(workingDirPath, "stdout.txt");
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(stdoutFile))) {                
                InputStream stdout = process.getInputStream ();
                BufferedReader reader = new BufferedReader (new InputStreamReader(stdout));
                String line = "";
                while ((line = reader.readLine ()) != null) {     
                    writer.write(line);  
                    writer.newLine();
                    writer.flush();
                }
                writer.write("#############################################################");
                writer.newLine();
                writer.write("END OF EXECUTION");
                writer.newLine();
                writer.write("#############################################################");
            } catch (IOException e) {                
                e.printStackTrace();
            }
        }
    }

    private static String getInstancePath(final String instance) {
        return SystemConstants.INSTANCES_DIR + instance;
    }

    private static synchronized Integer getNextInstanceId() {
        return ++instanceIdSequence;
    }

    private static String createInstanceDir(String name) {

        String instancePath =
                getInstancePath(name);
        deleteInstanceDir(name);
        File dir = new File(instancePath);
        dir.mkdirs();
        return instancePath;
    }

    private static void deleteInstanceDir(String name) {
        String instancePath =
                getInstancePath(name);
        File dir = new File(instancePath);
        if (dir.exists()) {
            FileUtils.deleteDir(dir);
        }
    }

    private static synchronized Boolean existsInstanceProcess(Integer id) {
        return processes.containsKey(id);
    }

    private static synchronized InstanceProcess getInstanceProcess(Integer id) {
        return processes.get(id);
    }

    private static synchronized void insertInstanceProcess(
            Integer id, InstanceProcess instanceProcess) {
        processes.put(id, instanceProcess);
    }

    private static synchronized void removeInstanceProcess(
            Integer id) {
        InstanceProcess instProc = processes.get(id);
        if(instProc == null)
            return;
        instProc.getProcess().destroyForcibly();
        processes.remove(id);
    }
}
