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

import app.client.ComputingApi;
import app.models.Body;
import app.models.PluginComputingInstanceModel;
import app.models.PluginComputingRegionModel;
import app.models.PluginComputingZoneModel;
import utils.TestUtils;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class ComputingControllerTest {
    
    @Autowired
    private ComputingController controller;
    @Value("${local.server.port}")
    private int PORT;        

    @Test
    public void contexLoads() throws Exception {
        assertThat(controller).isNotNull();
    }

    @Test
    public void CRUD_Test() throws Exception {
        PluginComputingInstanceModel instance = createInstance();   
        deleteInstance(instance);
    }

    @Test
    public void list_Regions_Test() throws Exception {
        ComputingApi api = new ComputingApi(TestUtils.getUrl(PORT));

        Supplier<Credentials> awsSupplier = TestUtils.createSupplier();

        Body<List<PluginComputingRegionModel>> body =
                api.listRegions(
                    awsSupplier.get().credential,
                    awsSupplier.get().identity);

        assertThat(body).isNotNull();
        assertThat(body.getContent()).isNotEmpty();
    }

    @Test
    public void list_Region_Zones_Test() throws Exception {
        ComputingApi api = new ComputingApi(TestUtils.getUrl(PORT));

        Supplier<Credentials> awsSupplier = TestUtils.createSupplier();

        Body<List<PluginComputingZoneModel>> body =
                api.listRegionZones(
                    awsSupplier.get().credential,
                    awsSupplier.get().identity,
                    "us-east-1");

        assertThat(body).isNotNull();
        assertThat(body.getContent()).isNotEmpty();
    }
    
    public void deleteInstance(PluginComputingInstanceModel instance) throws Exception {
        
        Supplier<Credentials> awsSupplier = TestUtils.createSupplier();
        ComputingApi api = new ComputingApi(TestUtils.getUrl(PORT));

        Body<Boolean> body =
                api.deleteInstance(
                    awsSupplier.get().credential,
                    awsSupplier.get().identity,
                    TestUtils.DEFAULT_REGION,
                    TestUtils.DEFAULT_ZONE, 
                    instance.getName());

        assertThat(body).isNotNull();
        assertThat(body.getContent()).isTrue();         
    }
    
    public PluginComputingInstanceModel createInstance() throws Exception {
        
        PluginComputingInstanceModel instance = 
                getInstanceToCreate();
        ComputingApi api = new ComputingApi(TestUtils.getUrl(PORT));

        Supplier<Credentials> awsSupplier = TestUtils.createSupplier();

        Body<PluginComputingInstanceModel> body =
                api.createInstance(
                    awsSupplier.get().credential,
                    awsSupplier.get().identity,
                    instance);
        
        assertThat(body).isNotNull();
        assertThat(body.getContent()).isNotNull();    
        
        return body.getContent();
    }
    
    private PluginComputingInstanceModel getInstanceToCreate() {
        
        List<Integer> ports = new ArrayList<>() ;
        ports.add(80);
        
        PluginComputingInstanceModel instance = new PluginComputingInstanceModel();
        instance.setImageUrl(TestUtils.FREE_TIER_IMAGE_NAME);
        instance.setStartupScript(TestUtils.INSTANCE_STARTUP_SCRIPT);
        instance.setType(TestUtils.FREE_TIER_INSTANCE_TYPE);
        instance.setRegion(TestUtils.DEFAULT_REGION);
        instance.setZone(TestUtils.DEFAULT_ZONE);        
        instance.setFirewallTcpPorts(ports);
        
        return instance;
    }
}
