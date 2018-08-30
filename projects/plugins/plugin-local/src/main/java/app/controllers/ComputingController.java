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

import app.common.GlobalConstants;
import app.common.OsUtil;
import app.common.SystemConstants;
import app.common.utils.FileUtils;
import app.models.Body;
import app.models.PluginComputingInstanceModel;
import app.models.PluginComputingRegionModel;
import app.models.PluginComputingZoneModel;

@RestController
public class ComputingController extends AbstractComputingController {

    private static final String STARTUP_SCRIPT_NAME = "startup_script";
    private static Integer instanceIdSequence = 0;
    private static Map<Integer, InstanceProcess> processes = new HashMap<>();

    /*
     * Overwritten Methods
     */

    @Override
    protected ResponseEntity<Body<List<PluginComputingInstanceModel>>> createInstances(
            final String token,
            final String identity,
            final List<PluginComputingInstanceModel> listModel) throws Exception {

        checkOphanInstances();

        for (PluginComputingInstanceModel pluginInstanceModel : listModel) {

            String instanceName = getNewName();
            Integer id = PluginComputingInstanceModel.extractIdFromName(
                            instanceName, GlobalConstants.BNZ_INSTANCE);

            String ip = System.getProperty(
                    SystemConstants.SYSTEM_PROPERTY_IP,
                    InetAddress.getLocalHost().getHostAddress());

            pluginInstanceModel.setId(String.valueOf(id));
            pluginInstanceModel.setName(instanceName);
            pluginInstanceModel.setExternalIp(ip);

            String instancePath =
                    createInstanceDir(instanceName);

            String startupScript =
                    generateStartupScript(
                            instancePath,
                            pluginInstanceModel.getStartupScript());

            Process process = Runtime.getRuntime().exec(
                    startupScript,
                    null,
                    new File(instancePath));

            InstanceProcess instanceProcess =
                    new InstanceProcess(instancePath, process, pluginInstanceModel);

            insertInstanceProcess(id, instanceProcess);
        }

        return ResponseEntity.ok(
                Body.create(listModel));
    }

    @Override
    protected ResponseEntity<Body<PluginComputingInstanceModel>> getInstance(
            final String token,
            final String identity,
            final String region,
            final String zone,
            final String name) throws Exception {

        checkOphanInstances();

        if(!SystemConstants.PLUGIN_ZONE.equals(zone)) {
            return new ResponseEntity<>(
                    Body.create(null),
                    HttpStatus.NOT_FOUND);
        }

        Integer instanceId =
                PluginComputingInstanceModel.extractIdFromName(
                        name, GlobalConstants.BNZ_INSTANCE);

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
            final String region,
            final String zone,
            final String name) throws Exception {

        checkOphanInstances();

        deleteInstanceDir(name);

        Integer instanceId =
                PluginComputingInstanceModel.extractIdFromName(
                        name, GlobalConstants.BNZ_INSTANCE);

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
    protected ResponseEntity<Body<List<PluginComputingInstanceModel>>> listInstances(
            final String token,
            final String identity) throws Exception {

        checkOphanInstances();

        List<PluginComputingInstanceModel> res = new ArrayList<>();
        for (Map.Entry<Integer, InstanceProcess> entry : processes.entrySet()) {
            res.add(entry.getValue().getPluginInstance());
        }

        return ResponseEntity.ok(
                Body.create(res));
    }

    /*
     * Overwritten Methods
     */
    @Override
    protected ResponseEntity<Body<List<PluginComputingRegionModel>>> listRegions(
            final String token,
            final String identity) throws Exception {

        List<PluginComputingRegionModel> res = new ArrayList<>();
        res.add(createRegionModel());
        return ResponseEntity.ok(
                Body.create(res));
    }
    
    @Override
    protected ResponseEntity<Body<List<PluginComputingZoneModel>>> listRegionZones(
            final String token,
            final String identity,
            final String name) throws Exception {        
        if(SystemConstants.PLUGIN_REGION.equals(name)) {
            List<PluginComputingZoneModel> res = new ArrayList<>();
            res.add(createZoneModel());
            return ResponseEntity.ok(
                    Body.create(res));
        }
        return new ResponseEntity<>(
                HttpStatus.NOT_FOUND);
    }

    private PluginComputingRegionModel createRegionModel() {
        return new PluginComputingRegionModel(
                        SystemConstants.PLUGIN_REGION);
    }

    public static PluginComputingZoneModel createZoneModel() {
        return new PluginComputingZoneModel(
                        SystemConstants.PLUGIN_ZONE);
    }

    private static class InstanceProcess implements Runnable{

        private String workingDirPath;
        private Process process;
        private PluginComputingInstanceModel pluginInstance;

        public InstanceProcess(
                String workingDirPath,
                Process process,
                PluginComputingInstanceModel pluginInstance) {
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
        public PluginComputingInstanceModel getPluginInstance() {
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

    private synchronized void checkOphanInstances() throws Exception {
        File instancesDir = new File(SystemConstants.INSTANCES_DIR);
        if(!instancesDir.exists()) {
            return;
        }

        // Insert process in memory that only exists your directory
        for (File file : instancesDir.listFiles()) {
            if (!file.isDirectory())
                continue;
            String instanceName = file.getName();
            Integer instanceId =
                    PluginComputingInstanceModel.extractIdFromName(
                            instanceName, GlobalConstants.BNZ_INSTANCE);
            if(!existsInstanceProcess(instanceId)) {
                insertFakeProcess(instanceId);
            }
        }

        // Removes from memory process that dont have a directory
        List<Integer> idsToRemove = new ArrayList<>();
        String instanceName = "";
        for (Integer id : processes.keySet()){
            instanceName =
                    PluginComputingInstanceModel.generateNameForId(
                            id, GlobalConstants.BNZ_INSTANCE);
            String instancePath =
                    getInstancePath(instanceName);
            File instaceDir = new File(instancePath);
            if(!instaceDir.exists()) {
                idsToRemove.add(id);
            }
        }
        for(Integer id : idsToRemove) {
            removeInstanceProcess(id);
        }
    }

    private String getNewName() throws Exception {

        String instanceName = "";
        while(true) {
            Integer newId = getNextInstanceId();
            instanceName =
                    PluginComputingInstanceModel.generateNameForId(
                            newId, GlobalConstants.BNZ_INSTANCE);
            String instancePath =
                    getInstancePath(instanceName);
            File instanceDir = new File(instancePath);
            if(!instanceDir.exists())
                break;
        }
        return instanceName;
    }

    protected String generateStartupScript(
            final String path,
            final String content) throws IOException {
        String extension =  OsUtil.getDefaultScriptExtension();
        File scriptFile = new File(path, STARTUP_SCRIPT_NAME + "." + extension);
        String absolutePath = scriptFile.getAbsolutePath();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(scriptFile))) {
            writer.write(content);
        }
        if(scriptFile.exists()) {
            FileUtils.setExecutionPermission(scriptFile);
        }
        return absolutePath;
    }

    private void insertFakeProcess(Integer id) throws Exception{

        PluginComputingInstanceModel pluginInstanceModel =
                new PluginComputingInstanceModel();
        String instanceName =
                PluginComputingInstanceModel.generateNameForId(
                        id, GlobalConstants.BNZ_INSTANCE);
        String ip = System.getProperty(
                SystemConstants.SYSTEM_PROPERTY_IP,
                InetAddress.getLocalHost().getHostAddress());
        String instancePath =
                getInstancePath(instanceName);

        pluginInstanceModel.setId(String.valueOf(id));
        pluginInstanceModel.setName(instanceName);
        pluginInstanceModel.setExternalIp(ip);
        pluginInstanceModel.setStartupScript("echo");
        Process process = Runtime.getRuntime().exec(
                pluginInstanceModel.getStartupScript(),
                null,
                new File(instancePath));

        InstanceProcess instanceProcess =
                new InstanceProcess(instancePath, process, pluginInstanceModel);

        insertInstanceProcess(id, instanceProcess);
    }

    private static String getInstancePath(final String instance) {
        return SystemConstants.INSTANCES_DIR + instance;
    }

    private static synchronized Integer getNextInstanceId() {
        return ++instanceIdSequence;
    }

    public static String createInstanceDir(String name) {

        String instancePath =
                getInstancePath(name);
        deleteInstanceDir(name);
        File dir = new File(instancePath);
        dir.mkdirs();
        return instancePath;
    }

    public static void deleteInstanceDir(String name) {
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
