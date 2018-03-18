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
import app.models.PluginInfoModel;

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
    public void getTest() throws Exception {
        
        InfoApi infoApi = new InfoApi(getUrl());  
        Body<PluginInfoModel> model = infoApi.getInfo();       
        
        String content = model.getMessage();        
        assertThat(content).isEqualTo(Body.OK);
    }
    
    private String getUrl() {
        return "http://localhost:"+PORT;
    }
}
