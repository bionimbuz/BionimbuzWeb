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
import org.springframework.web.client.RestClientException;

import app.client.ImageApi;
import app.models.Body;
import app.models.PluginImageModel;
import utils.TestUtils;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class ImageControllerTest {

    @Autowired
    private ImageController controller;
    @Value("${local.server.port}")
    private int PORT;

    @Test
    public void contexLoads() throws Exception {
        assertThat(controller).isNotNull();
    }

    @Test
    public void all_TestimagesTest() throws RestClientException, IOException {  
        ImageApi api = new ImageApi(TestUtils.getUrl(PORT));
        
        Body<List<PluginImageModel>> body = api.listImages("", "");        
        assertThat(body.getContent().size()).isEqualTo(1);      
        
        PluginImageModel curentImage = body.getContent().get(0);   
        Body<PluginImageModel> body2 = api.getImage("", "", curentImage.getName());  
        assertThat(body2.getContent()).isNotNull();              

        body2 = api.getImage("", "", "unknown");  
        assertThat(body2).isNull();              
    }
}
