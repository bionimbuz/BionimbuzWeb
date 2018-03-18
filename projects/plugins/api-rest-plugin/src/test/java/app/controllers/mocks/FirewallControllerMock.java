package app.controllers.mocks;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import app.controllers.AbstractFirewallController;
import app.models.Body;
import app.models.PluginFirewallModel;
import app.models.PluginFirewallModel.PROTOCOL;

@RestController
public class FirewallControllerMock extends AbstractFirewallController {

    public static final String RETURN_GET = "/";
    
    @Override
    protected ResponseEntity<Body<PluginFirewallModel>> replaceRule(String token, String identity, PluginFirewallModel model) throws Exception {
        return ResponseEntity.ok().body(
                Body.create(model));
    }
    @Override
    protected ResponseEntity<Body<PluginFirewallModel>> getRule(String token, String identity, String name) throws Exception {
        PluginFirewallModel model = new PluginFirewallModel();
        model.setName(name);
        return ResponseEntity.ok().body(
                Body.create(model));
    }
    @Override
    protected ResponseEntity<Body<Void>> deleteRule(String token, String identity, String name) throws Exception {        
        return ResponseEntity.ok().body(
                new Body<Void>(Body.OK)); 
    }
    @Override
    protected ResponseEntity<Body<List<PluginFirewallModel>>> listRules(String token, String identity) throws Exception {        
        List<PluginFirewallModel> listRes = new ArrayList<>();
        listRes.add(new PluginFirewallModel());
        listRes.add(new PluginFirewallModel());        
        return ResponseEntity.ok().body(
                Body.create(Body.OK, listRes));
    }        
    
    @RequestMapping(path = RETURN_GET+"/{status}", method = RequestMethod.GET)
    private ResponseEntity< Body<PluginFirewallModel> > returnError(
            @PathVariable(value="status") final int statusInt) {   
        
        HttpStatus status = HttpStatus.valueOf(statusInt);        
        PluginFirewallModel model = new PluginFirewallModel(
                PROTOCOL.tcp, 80, new ArrayList<String>());
        
        if(status == HttpStatus.OK) {            
            return ResponseEntity
                        .status(status)
                        .body(Body.create(model)); 
        }
        return ResponseEntity
                .status(status)
                .body(new Body<PluginFirewallModel>("Testing errors!"));        
    }    
}