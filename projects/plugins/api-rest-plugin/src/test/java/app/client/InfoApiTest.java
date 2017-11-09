package app.client;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;

import app.controllers.mocks.InfoControllerMock;
import app.models.Body;
import app.models.InfoModel;
import retrofit2.Call;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class InfoApiTest {
    
    @Autowired
    private InfoControllerMock controller;
    @Value("${local.server.port}")
    private int PORT = 0;
    
    @Test
    public void contexLoads() throws Exception {
        assertThat(controller).isNotNull();
    }
    
    @Test
    public void getInfoTest() throws Exception {
        
        PluginApi pluginApi = new PluginApi(getUrl());
        InfoApi infoApi = pluginApi.createApi(InfoApi.class);  
        Call<Body<InfoModel>> call = infoApi.getInfo();        
        Body<InfoModel> model = call.execute().body();
        
        String content = model.getMessage();        
        assertThat(content).isEqualTo(Body.OK);
    }
    
    private String getUrl() {
        return "http://localhost:"+PORT;
    }
}
