package app.common;

import org.openstack4j.api.OSClient;
import org.openstack4j.core.transport.Config;
import org.openstack4j.model.common.Identifier;
import org.openstack4j.openstack.OSFactory;

import java.util.HashMap;

public class OSClientHelper {

    public static OSClient.OSClientV3 getOSClient(String token, String host, String project_id) {
        return OSFactory.builderV3()
                .endpoint("http://" + host + ":5000/v3")
                .withConfig(Config.newConfig().withEndpointNATResolution(host))
                .scopeToProject(Identifier.byId(project_id))
                .token(token)
                .authenticate();
    }

    public static HashMap<String, String> retrieveCredentialData(String identity) {
        HashMap<String, String> credentialData = new HashMap<>();

        String[] parts = identity.split("/");
        credentialData.put("host", parts[0]);
        credentialData.put("project_id", parts[1]);

        return credentialData;
    }
}
