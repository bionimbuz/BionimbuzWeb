package app.controllers.mocks;

import org.springframework.web.bind.annotation.RestController;

import app.controllers.AbstractInfoController;

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

}