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
    protected ResponseEntity<Body<PluginComputingInstanceModel>> createInstance(
            final String token,
            final String identity,
            final PluginComputingInstanceModel model) throws Exception {

        checkOphanInstances();

        final String instanceName = getNewName();
        final Integer id = PluginComputingInstanceModel.extractIdFromName(
                instanceName, GlobalConstants.BNZ_INSTANCE);

        final String ip = System.getProperty(
                SystemConstants.SYSTEM_PROPERTY_IP,
                InetAddress.getLocalHost().getHostAddress());

        model.setId(String.valueOf(id));
        model.setName(instanceName);
        model.setExternalIp(ip);

        final String instancePath = createInstanceDir(instanceName);

        final String startupScript = generateStartupScript(
                instancePath,
                model.getStartupScript());

        final Process process = Runtime.getRuntime().exec(
                startupScript,
                null,
                new File(instancePath));

        final InstanceProcess instanceProcess = new InstanceProcess(instancePath, process, model);

        insertInstanceProcess(id, instanceProcess);

        return ResponseEntity.ok(
                Body.create(model));
    }

    @Override
    protected ResponseEntity<Body<PluginComputingInstanceModel>> getInstance(
            final String token,
            final String identity,
            final String region,
            final String zone,
            final String name) throws Exception {

        checkOphanInstances();

        if (!SystemConstants.PLUGIN_ZONE.equals(zone)) {
            return new ResponseEntity<>(
                    Body.create(null),
                    HttpStatus.NOT_FOUND);
        }

        final Integer instanceId = PluginComputingInstanceModel.extractIdFromName(
                name, GlobalConstants.BNZ_INSTANCE);

        final InstanceProcess instanceProcess = getInstanceProcess(instanceId);

        if (instanceProcess == null) {
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

        final Integer instanceId = PluginComputingInstanceModel.extractIdFromName(
                name, GlobalConstants.BNZ_INSTANCE);

        if (!existsInstanceProcess(instanceId)) {
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

        final List<PluginComputingInstanceModel> res = new ArrayList<>();
        for (final Map.Entry<Integer, InstanceProcess> entry : processes.entrySet()) {
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

        final List<PluginComputingRegionModel> res = new ArrayList<>();
        res.add(createRegionModel());
        return ResponseEntity.ok(
                Body.create(res));
    }

    @Override
    protected ResponseEntity<Body<List<PluginComputingZoneModel>>> listRegionZones(
            final String token,
            final String identity,
            final String name) throws Exception {
        if (SystemConstants.PLUGIN_REGION.equals(name)) {
            final List<PluginComputingZoneModel> res = new ArrayList<>();
            res.add(createZoneModel());
            return ResponseEntity.ok(
                    Body.create(res));
        }
        return new ResponseEntity<>(
                HttpStatus.NOT_FOUND);
    }

    private static PluginComputingRegionModel createRegionModel() {
        return new PluginComputingRegionModel(
                SystemConstants.PLUGIN_REGION);
    }

    public static PluginComputingZoneModel createZoneModel() {
        return new PluginComputingZoneModel(
                SystemConstants.PLUGIN_ZONE);
    }

    private static class InstanceProcess implements Runnable {

        private final String workingDirPath;
        private final Process process;
        private final PluginComputingInstanceModel pluginInstance;

        public InstanceProcess(
                final String workingDirPath,
                final Process process,
                final PluginComputingInstanceModel pluginInstance) {
            this.workingDirPath = workingDirPath;
            this.process = process;
            this.pluginInstance = pluginInstance;
            this.startWritingStdoutFile();
        }

        private void startWritingStdoutFile() {
            new Thread(this).start();
        }

        public Process getProcess() {
            return this.process;
        }

        public PluginComputingInstanceModel getPluginInstance() {
            return this.pluginInstance;
        }

        @Override
        public void run() {
            final File stdoutFile = new File(this.workingDirPath, "stdout.txt");
            try (
                 BufferedWriter writer = new BufferedWriter(new FileWriter(stdoutFile))) {
                final InputStream stdout = this.process.getInputStream();
                final BufferedReader reader = new BufferedReader(new InputStreamReader(stdout));
                String line = "";
                while ((line = reader.readLine()) != null) {
                    writer.write(line);
                    writer.newLine();
                    writer.flush();
                }
                writer.write("#############################################################");
                writer.newLine();
                writer.write("END OF EXECUTION");
                writer.newLine();
                writer.write("#############################################################");
            } catch (final IOException e) {
                e.printStackTrace();
            }
        }
    }

    private synchronized static void checkOphanInstances() throws Exception {
        final File instancesDir = new File(SystemConstants.INSTANCES_DIR);
        if (!instancesDir.exists()) {
            return;
        }

        // Insert process in memory that only exists your directory
        for (final File file : instancesDir.listFiles()) {
            if (!file.isDirectory()) {
                continue;
            }
            final String instanceName = file.getName();
            final Integer instanceId = PluginComputingInstanceModel.extractIdFromName(
                    instanceName, GlobalConstants.BNZ_INSTANCE);
            if (!existsInstanceProcess(instanceId)) {
                insertFakeProcess(instanceId);
            }
        }

        // Removes from memory process that dont have a directory
        final List<Integer> idsToRemove = new ArrayList<>();
        String instanceName = "";
        for (final Integer id : processes.keySet()) {
            instanceName = PluginComputingInstanceModel.generateNameForId(
                    id, GlobalConstants.BNZ_INSTANCE);
            final String instancePath = getInstancePath(instanceName);
            final File instaceDir = new File(instancePath);
            if (!instaceDir.exists()) {
                idsToRemove.add(id);
            }
        }
        for (final Integer id : idsToRemove) {
            removeInstanceProcess(id);
        }
    }

    private static String getNewName() throws Exception {

        String instanceName = "";
        while (true) {
            final Integer newId = getNextInstanceId();
            instanceName = PluginComputingInstanceModel.generateNameForId(
                    newId, GlobalConstants.BNZ_INSTANCE);
            final String instancePath = getInstancePath(instanceName);
            final File instanceDir = new File(instancePath);
            if (!instanceDir.exists()) {
                break;
            }
        }
        return instanceName;
    }

    protected static String generateStartupScript(
            final String path,
            final String content) throws IOException {
        final String extension = OsUtil.getDefaultScriptExtension();
        final File scriptFile = new File(path, STARTUP_SCRIPT_NAME + "." + extension);
        final String absolutePath = scriptFile.getAbsolutePath();
        try (
             BufferedWriter writer = new BufferedWriter(new FileWriter(scriptFile))) {
            writer.write(content);
        }
        if (scriptFile.exists()) {
            FileUtils.setExecutionPermission(scriptFile);
        }
        return absolutePath;
    }

    private static void insertFakeProcess(final Integer id) throws Exception {

        final PluginComputingInstanceModel pluginInstanceModel = new PluginComputingInstanceModel();
        final String instanceName = PluginComputingInstanceModel.generateNameForId(
                id, GlobalConstants.BNZ_INSTANCE);
        final String ip = System.getProperty(
                SystemConstants.SYSTEM_PROPERTY_IP,
                InetAddress.getLocalHost().getHostAddress());
        final String instancePath = getInstancePath(instanceName);

        pluginInstanceModel.setId(String.valueOf(id));
        pluginInstanceModel.setName(instanceName);
        pluginInstanceModel.setExternalIp(ip);
        pluginInstanceModel.setStartupScript("echo");
        final Process process = Runtime.getRuntime().exec(
                pluginInstanceModel.getStartupScript(),
                null,
                new File(instancePath));

        final InstanceProcess instanceProcess = new InstanceProcess(instancePath, process, pluginInstanceModel);

        insertInstanceProcess(id, instanceProcess);
    }

    private static String getInstancePath(final String instance) {
        return SystemConstants.INSTANCES_DIR + instance;
    }

    private static synchronized Integer getNextInstanceId() {
        return ++instanceIdSequence;
    }

    public static String createInstanceDir(final String name) {

        final String instancePath = getInstancePath(name);
        deleteInstanceDir(name);
        final File dir = new File(instancePath);
        dir.mkdirs();
        return instancePath;
    }

    public static void deleteInstanceDir(final String name) {
        final String instancePath = getInstancePath(name);
        final File dir = new File(instancePath);
        if (dir.exists()) {
            FileUtils.deleteDir(dir);
        }
    }

    private static synchronized Boolean existsInstanceProcess(final Integer id) {
        return processes.containsKey(id);
    }

    private static synchronized InstanceProcess getInstanceProcess(final Integer id) {
        return processes.get(id);
    }

    private static synchronized void insertInstanceProcess(
            final Integer id, final InstanceProcess instanceProcess) {
        processes.put(id, instanceProcess);
    }

    private static synchronized void removeInstanceProcess(
            final Integer id) {
        final InstanceProcess instProc = processes.get(id);
        if (instProc == null) {
            return;
        }
        instProc.getProcess().destroy();
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        instProc.getProcess().destroyForcibly();
        processes.remove(id);
    }
}
