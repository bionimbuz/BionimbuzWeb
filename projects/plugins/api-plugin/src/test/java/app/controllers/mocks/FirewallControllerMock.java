package app.controllers.mocks;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import app.controllers.AbstractFirewallController;
import app.models.FirewallModel;

@RestController
public class FirewallControllerMock extends AbstractFirewallController {
    @Override
    protected ResponseEntity<?> replaceRule(String token, String identity, FirewallModel model) throws Exception {
        throw new Exception("Unninplemented.");
    }
    @Override
    protected ResponseEntity<?> getRule(String token, String identity, String name) throws Exception {
        throw new Exception("Unninplemented.");
    }
    @Override
    protected ResponseEntity<?> deleteRule(String token, String identity, String name) throws Exception {
        throw new Exception("Unninplemented.");
    }
    @Override
    protected ResponseEntity<?> listRules(String token, String identity) throws Exception {
        throw new Exception("Unninplemented.");
    }        
}