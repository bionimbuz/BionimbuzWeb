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

import app.client.FirewallApi;
import app.models.Body;
import app.models.PluginFirewallModel;
import app.models.PluginFirewallModel.PROTOCOL;
import utils.TestUtils;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class FirewallControllerTest {

    @Autowired
    private FirewallController controller;
    @Value("${local.server.port}")
    private int PORT;
    
    private static String FAKE_FIREWALL = "fake-firewall-rule";

    @Test
    public void contexLoads() throws Exception {
        assertThat(controller).isNotNull();
    }

    @Test
	public void deleteRuleTest() throws IOException {
        FirewallApi api = new FirewallApi(TestUtils.getUrl(PORT));
        Body<Boolean> body = api.deleteRule("", "", FAKE_FIREWALL);
        assertThat(body).isNotNull();
        assertThat(body.getContent()).isFalse();
	}
    
	@Test
	public void replaceRuleTest() throws IOException {	    
        FirewallApi api = new FirewallApi(TestUtils.getUrl(PORT));
        PluginFirewallModel firewall = 
                new PluginFirewallModel(PROTOCOL.tcp, 8080, null);
        Body<PluginFirewallModel> body = api.replaceRule("", "", firewall);
        assertThat(body).isNull();    
	}

	@Test
	public void getRuleTest() throws IOException { 
        FirewallApi api = new FirewallApi(TestUtils.getUrl(PORT));
        PluginFirewallModel firewall = 
                new PluginFirewallModel(PROTOCOL.tcp, 8080, null);
        Body<PluginFirewallModel> body = api.getRule("", "", firewall.getName());
        assertThat(body).isNull();    
	}

    @Test
    public void listAllTest() throws IOException {			
        FirewallApi api = new FirewallApi(TestUtils.getUrl(PORT));
        Body<List<PluginFirewallModel>> body = api.listRules("", "");
        assertThat(body).isNotNull();   
        assertThat(body.getContent().isEmpty());
	}    
}
