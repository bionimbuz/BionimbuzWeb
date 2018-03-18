package app.controllers.mocks;

import org.springframework.web.bind.annotation.RestController;

import app.controllers.AbstractInfoController;
import app.models.PluginInfoModel.AuthenticationType;

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
    @Override
    protected String getWriteScope() {
        return "Write Scope";
    }
    @Override
    protected String getReadScope() {
        return "Read Scope";
    }
}