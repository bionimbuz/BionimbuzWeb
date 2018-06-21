package app.controllers;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;

import app.client.InfoApi;
import app.common.SystemConstants;
import app.models.Body;
import app.models.PluginInfoModel;
import utils.TestUtils;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class InfoControllerTest {

    @Autowired
    private InfoController controller;
    @Value("${local.server.port}")
    private int PORT;

    @Test
    public void contexLoads() throws Exception {
        assertThat(controller).isNotNull();
    }

    @Test
    public void infoTest() throws IOException {
        InfoApi api = new InfoApi(TestUtils.getUrl(PORT));
        Body<PluginInfoModel> body = 
                api.getInfo();
        
        assertThat(body).isNotNull();
        assertThat(body.getContent().getPluginVersion())
                .isEqualTo(SystemConstants.PLUGIN_VERSION);
    }

}
