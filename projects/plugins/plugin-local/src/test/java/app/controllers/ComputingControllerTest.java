package app.controllers;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import app.client.ComputingApi;
import app.common.FileUtils;
import app.common.GlobalConstants;
import app.common.SystemConstants;
import app.models.Body;
import app.models.PluginComputingInstanceModel;
import app.models.PluginComputingRegionModel;
import app.models.PluginComputingZoneModel;
import utils.TestUtils;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class ComputingControllerTest {

    private static final Integer STARTUP_SCRIPT_APACHE_PORT = 80;
    private static final Integer WAIT_MS_TO_START_INSTANCE = 60 * 1000;
    private static final Integer LENGTH_CREATION = 2;
    private static final String INSTANCE_STARTUP_SCRIPT = "apt-get update && apt-get install -y apache2 && hostname > /var/www/index.html";

    @Autowired
    private ComputingController controller;
    @Autowired
    private TestRestTemplate restTemplate;
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

        List<PluginComputingInstanceModel> listToCreate =
                    getInstancesToCreate(LENGTH_CREATION);

        checkCurrentInstancesSize(0);
        listToCreate = createInstancesTest(listToCreate);
        checkGetInstances(listToCreate);
        checkCurrentInstancesSize(LENGTH_CREATION);
        deleteInstances(listToCreate);
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

    private void deleteInstances(List<PluginComputingInstanceModel> instances) throws IOException {

        ComputingApi api = new ComputingApi(TestUtils.getUrl(PORT));
        for (PluginComputingInstanceModel pluginInstanceModel : instances) {
            Body<Boolean> body = api.deleteInstance("", "",
                    SystemConstants.PLUGIN_ZONE,
                    pluginInstanceModel.getName());
            assertThat(body).isNotNull();
            assertThat(body.getContent()).isTrue();
        }
    }

    private void checkGetInstances(List<PluginComputingInstanceModel> instances) throws IOException {

        ComputingApi api = new ComputingApi(TestUtils.getUrl(PORT));
        for (PluginComputingInstanceModel pluginInstanceModel : instances) {
            Body<PluginComputingInstanceModel> body =
                    api.getInstance("", "",
                            SystemConstants.PLUGIN_ZONE,
                            pluginInstanceModel.getName());
            assertThat(body).isNotNull();
            assertThat(body.getContent()).isNotNull();
        }
    }

    private void checkCurrentInstancesSize(int size) throws IOException {

        ComputingApi api = new ComputingApi(TestUtils.getUrl(PORT));
        Body<List<PluginComputingInstanceModel>> body =
                api.listInstances("", "");

        assertThat(body.getContent().size()).isEqualTo(size);
    }

    private List<PluginComputingInstanceModel> createInstancesTest(List<PluginComputingInstanceModel> instances) throws IOException{

        ComputingApi api = new ComputingApi(TestUtils.getUrl(PORT));
        Body<List<PluginComputingInstanceModel>> body =
                api.createInstances("", "", instances);
        assertThat(body).isNotNull();
        assertThat(body.getContent().size()).isEqualTo(LENGTH_CREATION);

        return body.getContent();
    }

    private List<PluginComputingInstanceModel> getInstancesToCreate(int length) {
        List<PluginComputingInstanceModel> instances = new ArrayList<>();

        for(int i=0;i<length;i++) {
            PluginComputingInstanceModel instance = new PluginComputingInstanceModel();
            instance.setImageUrl("");
            instance.setStartupScript(INSTANCE_STARTUP_SCRIPT);
            instance.setType(SystemConstants.CLOUD_COMPUTE_TYPE);
            instance.setRegion(SystemConstants.PLUGIN_REGION);
            instance.setZone(SystemConstants.PLUGIN_ZONE);

            instances.add(instance);
        }

        return instances;
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
                        SystemConstants.PLUGIN_ZONE,
                        instanceName);
        assertThat(body).isNull();

        // Force directory creation
        ComputingController.createInstanceDir(instanceName);
        body = api.getInstance("", "",
                SystemConstants.PLUGIN_ZONE,
                instanceName);
        assertThat(body).isNotNull();
        assertThat(body.getContent()).isNotNull();

        // Force directory exclusion again
        ComputingController.deleteInstanceDir(instanceName);
        body = api.getInstance("", "",
                        SystemConstants.PLUGIN_ZONE,
                        instanceName);
        assertThat(body).isNull();
    }
}
