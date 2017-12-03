package app.controllers.mocks;

import org.springframework.web.bind.annotation.RestController;

import app.controllers.AbstractInfoController;
import app.models.InfoModel.AuthenticationType;

@RestController
public class InfoControllerMock extends AbstractInfoController {
    @Override
    protected String getCloudType() {
        return "type";
    }
    @Override
    protected String getPluginVersion() {
        return "version";
    }    
    @Override
    protected AuthenticationType getAuthenticationType() {
        return AuthenticationType.AUTH_BEARER_TOKEN;
    }
    @Override
    protected String getName() {
        return "GCP";
    }
}