package app.controllers;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.HashMap;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import app.client.PricingApi;
import app.models.Body;
import app.models.PluginPriceModel;
import app.models.PluginPriceTableModel;
import app.models.PluginPriceTableStatusModel;
import app.models.PluginPriceTableStatusModel.Status;
import app.models.pricing.InstanceTypePricing;
import app.models.pricing.StoragePricing;
import utils.TestUtils;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class PricingControllerTest {

    @Autowired
    private PricingController controller;
    @Autowired
    private TestRestTemplate restTemplate;
    @Value("${local.server.port}")
    private int PORT;

    @Test
    public void contexLoads() throws Exception {
        assertThat(controller).isNotNull();
    }

    @Test
    public void getPricingTest() throws IOException {  
        PricingApi api = new PricingApi(TestUtils.getUrl(PORT));
        Body<PluginPriceTableModel> bodyPrice = api.getPricing();
        
        PluginPriceTableStatusModel status =
                bodyPrice.getContent().getStatus();        
        assertThat(status.getStatus()).isEqualTo(Status.OK);
        assertThat(status.getLastSearch()).isNotNull();

        PluginPriceModel price = 
                bodyPrice.getContent().getPrice();
        assertThat(price.getLastUpdate()).isNotNull();
        
        HashMap<String, InstanceTypePricing> listInstPrice =
                price.getListInstancePricing();
        assertThat(listInstPrice).isNotEmpty();
        InstanceTypePricing typePricing = listInstPrice.entrySet().iterator().next().getValue();
        assertThat(typePricing.getListRegionPricing()).isNotEmpty();
        Double typePrice = typePricing.getListRegionPricing().entrySet().iterator().next().getValue();
        assertThat(typePrice).isEqualTo(0d);
        
        HashMap<String, StoragePricing> listStorPrice =
                price.getListStoragePricing();
        assertThat(listStorPrice).isNotEmpty();  
        StoragePricing storagePricing = 
                listStorPrice.entrySet().iterator().next().getValue();
        assertThat(storagePricing.getPrice()).isEqualTo(0d);
        assertThat(storagePricing.getClassAPrice()).isEqualTo(0d);
        assertThat(storagePricing.getClassBPrice()).isEqualTo(0d);        
    }
}
