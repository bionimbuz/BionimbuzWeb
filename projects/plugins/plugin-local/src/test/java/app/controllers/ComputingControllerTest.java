package app.controllers;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;

import app.client.ComputingApi;
import app.common.GlobalConstants;
import app.common.SystemConstants;
import app.common.utils.FileUtils;
import app.models.Body;
import app.models.PluginComputingInstanceModel;
import app.models.PluginComputingRegionModel;
import app.models.PluginComputingZoneModel;
import utils.TestUtils;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class ComputingControllerTest {

    private static final String INSTANCE_STARTUP_SCRIPT = "apt-get update && apt-get install -y apache2 && hostname > /var/www/index.html";

    @Autowired
    private ComputingController controller;
    @Value("${local.server.port}")
    private int PORT;

    @Before
    public void init() {
        File file = new File(SystemConstants.INSTANCES_DIR);
        FileUtils.deleteDir(file);
    }

    @Test
    public void contexLoads() throws Exception {
        assertThat(controller).isNotNull();
    }

    @Test
    public void CRUD_Instances() throws IOException {

        PluginComputingInstanceModel instance =
                    createInstance();

        checkCurrentInstancesSize(0);        
        instance = createInstanceTest(instance);
        checkGetInstance(instance);
        checkCurrentInstancesSize(1);
        deleteInstance(instance);
        checkCurrentInstancesSize(0);
    }
    
    @Test
    public void regions_zones_test() throws IOException {    
        ComputingApi api = new ComputingApi(TestUtils.getUrl(PORT));
        Body<List<PluginComputingRegionModel>> body = api.listRegions("", "");
        assertThat(body).isNotNull();   
        assertThat(body.getContent().isEmpty()).isFalse();
        
        PluginComputingRegionModel found = body.getContent().get(0);

        ComputingApi api2 = new ComputingApi(TestUtils.getUrl(PORT));
        Body<List<PluginComputingZoneModel>> body2 = api2.listRegionZones("", "", found.getName());
        assertThat(body2).isNotNull();   
        assertThat(body2.getContent().isEmpty()).isFalse();
        
        body2 = api2.listRegionZones("", "", "unknown");
        assertThat(body2).isNull();      
    }

    @Test
    public void list_zones_Test() throws IOException {    
    }

    private void deleteInstance(PluginComputingInstanceModel instance) throws IOException {

        ComputingApi api = new ComputingApi(TestUtils.getUrl(PORT));
        Body<Boolean> body = api.deleteInstance("", "",
                SystemConstants.PLUGIN_REGION,
                SystemConstants.PLUGIN_ZONE,
                instance.getName());
        assertThat(body).isNotNull();
        assertThat(body.getContent()).isTrue();
    }

    private void checkGetInstance(PluginComputingInstanceModel instance) throws IOException {

        ComputingApi api = new ComputingApi(TestUtils.getUrl(PORT));
        Body<PluginComputingInstanceModel> body =
                api.getInstance("", "",
                        SystemConstants.PLUGIN_REGION,
                        SystemConstants.PLUGIN_ZONE,
                        instance.getName());
        assertThat(body).isNotNull();
        assertThat(body.getContent()).isNotNull();
    }

    private void checkCurrentInstancesSize(int size) throws IOException {

        ComputingApi api = new ComputingApi(TestUtils.getUrl(PORT));
        Body<List<PluginComputingInstanceModel>> body =
                api.listInstances("", "");

        assertThat(body.getContent().size()).isEqualTo(size);
    }
    
    private PluginComputingInstanceModel createInstanceTest(PluginComputingInstanceModel instance) throws IOException{

        ComputingApi api = new ComputingApi(TestUtils.getUrl(PORT));
        
        
        Body<PluginComputingInstanceModel> body =
                api.createInstance("", "", instance);
        assertThat(body).isNotNull();
        assertThat(body.getContent()).isNotNull();

        return body.getContent();
    }

    private PluginComputingInstanceModel createInstance() {
        PluginComputingInstanceModel instance = new PluginComputingInstanceModel();
        instance.setImageUrl("");
        instance.setStartupScript(INSTANCE_STARTUP_SCRIPT);
        instance.setType(SystemConstants.CLOUD_COMPUTE_TYPE);
        instance.setRegion(SystemConstants.PLUGIN_REGION);
        instance.setZone(SystemConstants.PLUGIN_ZONE);

        return instance;
    }

    @Test
    public void createInstanceBeforeExecution() throws Exception {

        ComputingApi api = new ComputingApi(TestUtils.getUrl(PORT));
        Body<PluginComputingInstanceModel> body = null;
        Integer newId = 999;
        String instanceName =
                PluginComputingInstanceModel.generateNameForId(
                        newId, GlobalConstants.BNZ_INSTANCE);

        // Force directory exclusion
        ComputingController.deleteInstanceDir(instanceName);
        body = api.getInstance("", "",
                SystemConstants.PLUGIN_REGION,
                SystemConstants.PLUGIN_ZONE,
                instanceName);
        assertThat(body).isNull();

        // Force directory creation
        ComputingController.createInstanceDir(instanceName);
        body = api.getInstance("", "",
                SystemConstants.PLUGIN_REGION,
                SystemConstants.PLUGIN_ZONE,
                instanceName);
        assertThat(body).isNotNull();
        assertThat(body.getContent()).isNotNull();

        // Force directory exclusion again
        ComputingController.deleteInstanceDir(instanceName);
        body = api.getInstance("", "",
                SystemConstants.PLUGIN_REGION,
                SystemConstants.PLUGIN_ZONE,
                instanceName);
        assertThat(body).isNull();
    }
}
