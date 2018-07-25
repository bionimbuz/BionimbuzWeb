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
    public void imagesTest() throws Exception {        

        ImageApi api = new ImageApi(TestUtils.getUrl(PORT));

        Supplier<Credentials> awsSupplier = TestUtils.createSupplier();

        Body<List<PluginImageModel>> body =
                api.listImages(
                    awsSupplier.get().credential,
                    awsSupplier.get().identity);
        
        assertThat(body).isNotNull();
        assertThat(body.getContent()).isNotEmpty();
    }
    
    @Test
    public void getTest() throws Exception {        

        ImageApi api = new ImageApi(TestUtils.getUrl(PORT));

        Supplier<Credentials> awsSupplier = TestUtils.createSupplier();

        Body<PluginImageModel> body =
                api.getImage(
                    awsSupplier.get().credential,
                    awsSupplier.get().identity,
                    TestUtils.FREE_TIER_IMAGE_NAME);
        
        assertThat(body).isNotNull();
    }
}
