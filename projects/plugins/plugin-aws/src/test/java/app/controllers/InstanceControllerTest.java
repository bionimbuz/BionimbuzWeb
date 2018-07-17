package app.controllers;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.jclouds.domain.Credentials;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;

import com.google.common.base.Supplier;

import app.client.InstanceApi;
import app.models.Body;
import app.models.PluginInstanceModel;
import utils.TestUtils;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class InstanceControllerTest {
    
    @Autowired
    private InstanceController controller;
    @Value("${local.server.port}")
    private int PORT;        

    @Test
    public void contexLoads() throws Exception {
        assertThat(controller).isNotNull();
    }

    @Test
    public void createInstance() throws Exception {
        
        List<PluginInstanceModel> listInstance = 
                new ArrayList<>();
        listInstance.add(getInstancesToCreate());
        InstanceApi api = new InstanceApi(TestUtils.getUrl(PORT));

        Supplier<Credentials> awsSupplier = TestUtils.createSupplier();

        Body<List<PluginInstanceModel>> body =
                api.createInstance(
                    awsSupplier.get().credential,
                    awsSupplier.get().identity,
                    listInstance);
        
        assertThat(body).isNotNull();
        assertThat(body.getContent()).isNotEmpty();        
    }
    
    private PluginInstanceModel getInstancesToCreate() {
        PluginInstanceModel instance = new PluginInstanceModel();
        instance.setImageUrl(TestUtils.FREE_TIER_IMAGE_NAME);
        instance.setStartupScript(TestUtils.INSTANCE_STARTUP_SCRIPT);
        instance.setType(TestUtils.FREE_TIER_INSTANCE_TYPE);
        instance.setRegion(TestUtils.DEFAULT_REGION);
        instance.setZone(TestUtils.DEFAULT_ZONE);        
        return instance;
    }
}
