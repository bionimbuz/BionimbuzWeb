package app.controllers;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.google.common.net.HttpHeaders;

import app.common.HttpHeadersCustom;
import app.common.Routes;
import app.models.Body;
import app.models.PluginFirewallModel;

public abstract class AbstractFirewallController extends BaseControllerVersioned{
    protected static final Logger LOGGER = LoggerFactory.getLogger(AbstractFirewallController.class);

    /*
     * Action Methods
     */

    @RequestMapping(path = Routes.FIREWALLS, method = RequestMethod.POST)
    private ResponseEntity< Body<PluginFirewallModel> > replaceRuleAction(
            @RequestHeader(value=HttpHeadersCustom.API_VERSION) final String version,
            @RequestHeader(value=HttpHeaders.AUTHORIZATION) final String token,
            @RequestHeader(value=HttpHeadersCustom.AUTHORIZATION_ID) final String identity,
            @RequestBody PluginFirewallModel model) {
        return callImplementedMethod("replaceRule", version, token, identity, model);
    }
	@RequestMapping(path = Routes.FIREWALLS_NAME_, method = RequestMethod.GET)
	private ResponseEntity< Body<PluginFirewallModel> > getRuleAction(
            @RequestHeader(value=HttpHeadersCustom.API_VERSION) final String version,
            @RequestHeader(value=HttpHeaders.AUTHORIZATION) final String token,
            @RequestHeader(value=HttpHeadersCustom.AUTHORIZATION_ID) final String identity,
    		@PathVariable(value="name") final String name) {
        return callImplementedMethod("getRule", version, token, identity, name);
    }
    @RequestMapping(path = Routes.FIREWALLS_NAME_, method = RequestMethod.DELETE)
    private ResponseEntity< Body<Boolean> > deleteRuleAction(
            @RequestHeader(value=HttpHeadersCustom.API_VERSION) final String version,
            @RequestHeader(value=HttpHeaders.AUTHORIZATION) final String token,
            @RequestHeader(value=HttpHeadersCustom.AUTHORIZATION_ID) final String identity,
    		@PathVariable(value="name") final String name) {
        return callImplementedMethod("deleteRule", version, token, identity, name);
    }
    @RequestMapping(path = Routes.FIREWALLS, method = RequestMethod.GET)
    private ResponseEntity< Body<List<PluginFirewallModel> >> listRulesAction(
            @RequestHeader(value=HttpHeadersCustom.API_VERSION) final String version,
            @RequestHeader(value=HttpHeaders.AUTHORIZATION) final String token,
            @RequestHeader(value=HttpHeadersCustom.AUTHORIZATION_ID) final String identity) {
        return callImplementedMethod("listRules", version, token, identity);
    }

    /*
     * Abstract Methods
     */

    protected abstract ResponseEntity<Body<PluginFirewallModel>> replaceRule(
            final String token,
            final String identity,
            PluginFirewallModel model) throws Exception;
    protected abstract ResponseEntity<Body<PluginFirewallModel>> getRule(
            final String token,
            final String identity,
            final String name) throws Exception;
    protected abstract ResponseEntity<Body<Boolean>> deleteRule(
            final String token,
            final String identity,
            final String name) throws Exception;
    protected abstract ResponseEntity<Body<List<PluginFirewallModel>>> listRules(
            final String token,
            final String identity) throws Exception;
}
