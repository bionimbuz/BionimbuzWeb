package app.client;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;

import app.common.GlobalConstants;
import app.controllers.mocks.InstanceControllerMock;
import app.models.Body;
import app.models.ImageModel;
import retrofit2.Call;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class ImageApiTest {
    
    @Autowired
    private InstanceControllerMock controller;
    @Value("${local.server.port}")
    private int PORT = 0;
    
    @Test
    public void contexLoads() throws Exception {
        assertThat(controller).isNotNull();
    }
        

    @Test
    public void listTest() throws Exception {
        ImageApi instanceApi = createApi();
        Call<Body<List<ImageModel>>> call = 
                instanceApi.listImages(
                        GlobalConstants.API_VERSION, 
                        "fake-token", "fake-identity");
        Body<List<ImageModel>> body = call.execute().body();
        assertThat(body.getMessage()).isEqualTo(Body.OK);
        assertThat(body.getContent()).isNotNull();
        assertThat(body.getContent()).isNotEmpty();
    }
    
    private String getUrl() {
        return "http://localhost:"+PORT;
    }
    
    private ImageApi createApi() {
        PluginApi pluginApi = new PluginApi(getUrl());
        return pluginApi.createApi(ImageApi.class); 
    }    
}
