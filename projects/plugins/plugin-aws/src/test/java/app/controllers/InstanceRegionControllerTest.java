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

import app.client.InstanceRegionApi;
import app.models.Body;
import app.models.PluginInstanceRegionModel;
import app.models.PluginInstanceZoneModel;
import utils.TestUtils;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class InstanceRegionControllerTest {

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
        InstanceRegionApi api = new InstanceRegionApi(TestUtils.getUrl(PORT));

        Supplier<Credentials> awsSupplier = TestUtils.createSupplier();

        Body<List<PluginInstanceRegionModel>> body =
                api.listInstanceRegions(
                    awsSupplier.get().credential,
                    awsSupplier.get().identity);

        assertThat(body).isNotNull();
        assertThat(body.getContent()).isNotEmpty();
    }

    @Test
    public void list_filtered_Test() throws Exception {
        InstanceRegionApi api = new InstanceRegionApi(TestUtils.getUrl(PORT));

        Supplier<Credentials> awsSupplier = TestUtils.createSupplier();

        Body<List<PluginInstanceZoneModel>> body =
                api.listInstanceRegionsZones(
                    awsSupplier.get().credential,
                    awsSupplier.get().identity,
                    "us-east-1");

        assertThat(body).isNotNull();
        assertThat(body.getContent()).isNotEmpty();
    }

}