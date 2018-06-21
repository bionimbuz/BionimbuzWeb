package app.controllers;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;

import app.client.InstanceRegionApi;
import app.models.Body;
import app.models.PluginInstanceRegionModel;
import app.models.PluginInstanceZoneModel;
import utils.TestUtils;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class InstanceRegionControllerTest {
    
    @Autowired
    private InstanceController controller;
    @Value("${local.server.port}")
    private int PORT;        

    @Test
    public void contexLoads() throws Exception {
        assertThat(controller).isNotNull();
    }

    @Test
    public void list_all() throws IOException {    
        InstanceRegionApi api = new InstanceRegionApi(TestUtils.getUrl(PORT));
        Body<List<PluginInstanceRegionModel>> body = api.listInstanceRegions("", "");
        assertThat(body).isNotNull();   
        assertThat(body.getContent().isEmpty()).isFalse();
        
        PluginInstanceRegionModel found = body.getContent().get(0);

        InstanceRegionApi api2 = new InstanceRegionApi(TestUtils.getUrl(PORT));
        Body<List<PluginInstanceZoneModel>> body2 = api2.listInstanceRegionsZones("", "", found.getName());
        assertThat(body2).isNotNull();   
        assertThat(body2.getContent().isEmpty()).isFalse();
        
        body2 = api2.listInstanceRegionsZones("", "", "unknown");
        assertThat(body2).isNull();      
    }

    @Test
    public void list_zones_Test() throws IOException {    
    }

}
