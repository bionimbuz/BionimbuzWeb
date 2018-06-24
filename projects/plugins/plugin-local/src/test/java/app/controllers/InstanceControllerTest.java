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

import app.client.InstanceApi;
import app.common.FileUtils;
import app.common.SystemConstants;
import app.models.Body;
import app.models.PluginInstanceModel;
import utils.TestUtils;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class InstanceControllerTest {

    private static final Integer STARTUP_SCRIPT_APACHE_PORT = 80;
    private static final Integer WAIT_MS_TO_START_INSTANCE = 60 * 1000;
    private static final Integer LENGTH_CREATION = 2;

    @Autowired
    private InstanceController controller;
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
    public void CRUD_Test() throws IOException {

        List<PluginInstanceModel> listToCreate =
                    getInstancesToCreate(LENGTH_CREATION);

        checkCurrentInstancesSize(0);
        listToCreate = createInstancesTest(listToCreate);
        checkGetInstances(listToCreate);
        checkCurrentInstancesSize(LENGTH_CREATION);
        deleteInstances(listToCreate);
        checkCurrentInstancesSize(0);
    }

    private void deleteInstances(List<PluginInstanceModel> instances) throws IOException {

        InstanceApi api = new InstanceApi(TestUtils.getUrl(PORT));
        for (PluginInstanceModel pluginInstanceModel : instances) {
            Body<Boolean> body = api.deleteInstance("", "",
                    SystemConstants.PLUGIN_ZONE,
                    pluginInstanceModel.getName());
            assertThat(body).isNotNull();
            assertThat(body.getContent()).isTrue();
        }
    }

    private void checkGetInstances(List<PluginInstanceModel> instances) throws IOException {

        InstanceApi api = new InstanceApi(TestUtils.getUrl(PORT));
        for (PluginInstanceModel pluginInstanceModel : instances) {
            Body<PluginInstanceModel> body =
                    api.getInstance("", "",
                            SystemConstants.PLUGIN_ZONE,
                            pluginInstanceModel.getName());
            assertThat(body).isNotNull();
            assertThat(body.getContent()).isNotNull();
        }
    }

    private void checkCurrentInstancesSize(int size) throws IOException {

        InstanceApi api = new InstanceApi(TestUtils.getUrl(PORT));
        Body<List<PluginInstanceModel>> body =
                api.listInstances("", "");

        assertThat(body.getContent().size()).isEqualTo(size);
    }

    private List<PluginInstanceModel> createInstancesTest(List<PluginInstanceModel> instances) throws IOException{

        InstanceApi api = new InstanceApi(TestUtils.getUrl(PORT));
        Body<List<PluginInstanceModel>> body =
                api.createInstance("", "", instances);
        assertThat(body).isNotNull();
        assertThat(body.getContent().size()).isEqualTo(LENGTH_CREATION);

        return body.getContent();
    }

    private List<PluginInstanceModel> getInstancesToCreate(int length) {
        List<PluginInstanceModel> instances = new ArrayList<>();

        for(int i=0;i<length;i++) {
            PluginInstanceModel instance = new PluginInstanceModel();
            instance.setImageUrl("");
            instance.setStartupScript("touch file_1 \n\r touch file_2 \r\n");
            instance.setType(SystemConstants.CLOUD_COMPUTE_TYPE);
            instance.setRegion(SystemConstants.PLUGIN_REGION);
            instance.setZone(SystemConstants.PLUGIN_ZONE);

            instances.add(instance);
        }

        return instances;
    }

}
