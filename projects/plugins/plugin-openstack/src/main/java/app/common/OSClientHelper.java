package app.common;

import org.openstack4j.api.OSClient;
import org.openstack4j.core.transport.Config;
import org.openstack4j.model.common.Identifier;
import org.openstack4j.openstack.OSFactory;

import static app.common.SystemConstants.HOST;
import static app.common.SystemConstants.KEYSTONE_HOST;
import static app.common.SystemConstants.TEST_PROJECT_ID;

public class OSClientHelper {

    private static final Identifier PROJECT = Identifier.byId(TEST_PROJECT_ID);

    public static OSClient.OSClientV3 getOSClient(String token) {
        return OSFactory.builderV3()
                .endpoint(KEYSTONE_HOST)
                .withConfig(Config.newConfig().withEndpointNATResolution(HOST))
                .scopeToProject(PROJECT)
                .token(token)
                .authenticate();
    }
}
