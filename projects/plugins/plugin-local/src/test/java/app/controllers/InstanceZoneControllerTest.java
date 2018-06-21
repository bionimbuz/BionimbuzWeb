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

import app.client.InstanceZoneApi;
import app.models.Body;
import app.models.PluginInstanceZoneModel;
import utils.TestUtils;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class InstanceZoneControllerTest {
    
    @Autowired
    private InstanceController controller;
    @Value("${local.server.port}")
    private int PORT;        

    @Test
    public void contexLoads() throws Exception {
        assertThat(controller).isNotNull();
    }

    @Test
    public void list_Test() throws IOException {        
        InstanceZoneApi api = new InstanceZoneApi(TestUtils.getUrl(PORT));
        Body<List<PluginInstanceZoneModel>> body = api.listInstanceZones("", "");
        assertThat(body).isNotNull();   
        assertThat(body.getContent().isEmpty()).isFalse();
    }
}
