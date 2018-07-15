package app.controllers;

import static org.assertj.core.api.Assertions.assertThat;

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

import app.client.InstanceZoneApi;
import app.models.Body;
import app.models.PluginInstanceZoneModel;
import utils.TestUtils;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class InstanceZoneControllerTest {

    @Autowired
    private InstanceRegionController controller;
    @Value("${local.server.port}")
    private int PORT;

    @Test
    public void contexLoads() throws Exception {
        assertThat(controller).isNotNull();
    }

    @Test
    public void list_Test() throws Exception {
        InstanceZoneApi api = new InstanceZoneApi(TestUtils.getUrl(PORT));

        Supplier<Credentials> awsSupplier = TestUtils.createSupplier();

        Body<List<PluginInstanceZoneModel>> body =
                api.listInstanceZones(
                    awsSupplier.get().credential,
                    awsSupplier.get().identity);

        assertThat(body).isNotNull();
        assertThat(body.getContent()).isNotEmpty();
    }
}
