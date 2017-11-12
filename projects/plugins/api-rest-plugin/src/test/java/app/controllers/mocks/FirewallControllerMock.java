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
import app.models.FirewallModel;
import app.models.FirewallModel.PROTOCOL;

@RestController
public class FirewallControllerMock extends AbstractFirewallController {

    public static final String RETURN_GET = "/";
    
    @Override
    protected ResponseEntity<Body<FirewallModel>> replaceRule(String token, String identity, FirewallModel model) throws Exception {
        return ResponseEntity.ok().body(
                Body.create(model));
    }
    @Override
    protected ResponseEntity<Body<FirewallModel>> getRule(String token, String identity, String name) throws Exception {
        FirewallModel model = new FirewallModel();
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
    protected ResponseEntity<Body<List<FirewallModel>>> listRules(String token, String identity) throws Exception {        
        List<FirewallModel> listRes = new ArrayList<>();
        listRes.add(new FirewallModel());
        listRes.add(new FirewallModel());        
        return ResponseEntity.ok().body(
                Body.create(Body.OK, listRes));
    }        
    
    @RequestMapping(path = RETURN_GET+"/{status}", method = RequestMethod.GET)
    private ResponseEntity< Body<FirewallModel> > returnError(
            @PathVariable(value="status") final int statusInt) {   
        
        HttpStatus status = HttpStatus.valueOf(statusInt);        
        FirewallModel model = new FirewallModel(
                PROTOCOL.tcp, 80, new ArrayList<String>());
        
        if(status == HttpStatus.OK) {            
            return ResponseEntity
                        .status(status)
                        .body(Body.create(model)); 
        }
        return ResponseEntity
                .status(status)
                .body(new Body<FirewallModel>("Testing errors!"));        
    }    
}