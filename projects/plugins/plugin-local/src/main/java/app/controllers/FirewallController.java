package app.controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import app.models.Body;
import app.models.PluginFirewallModel;

/*
 * Firewall rules cannot be created with Java.
 * Assert that the necessary rules was previously
 * created.
 */
@RestController
public class FirewallController extends AbstractFirewallController{

    /*
     * Overwritten Methods
     */

    @Override
    protected ResponseEntity<Body<PluginFirewallModel>> replaceRule(
            final String token,
            final String identity,
            PluginFirewallModel model) throws Exception {
        return new ResponseEntity<>(
                HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Body<PluginFirewallModel>> getRule(
            final String token,
            final String identity,
    		final String name) throws Exception {
        return new ResponseEntity<>(
                HttpStatus.NOT_FOUND);
    }

    @Override
    protected ResponseEntity<Body<Boolean>> deleteRule(
            final String token,
            final String identity,
    		final String name) throws Exception  {
        return new ResponseEntity<>(
                Body.create(false),
                HttpStatus.OK);
    }

    @Override
    protected ResponseEntity<Body<List<PluginFirewallModel>>> listRules(
            final String token,
            final String identity) throws Exception  {
        return ResponseEntity.ok(
                Body.create(new ArrayList<>()));
    }
}
