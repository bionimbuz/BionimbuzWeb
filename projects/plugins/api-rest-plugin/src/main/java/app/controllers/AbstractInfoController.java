package app.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import app.common.GlobalConstants;
import app.common.Routes;
import app.models.Body;
import app.models.PluginInfoModel;
import app.models.PluginInfoModel.AuthenticationType;

public abstract class AbstractInfoController extends BaseControllerVersioned {
    protected static final Logger LOGGER = LoggerFactory.getLogger(AbstractInfoController.class);

    /*
     * Action Methods
     */

    @RequestMapping(path = Routes.INFO, method = RequestMethod.GET)
    private ResponseEntity< Body<PluginInfoModel> > getInfoAction() {

    	PluginInfoModel model = new PluginInfoModel(GlobalConstants.API_VERSION);
    	model.setCloudType(getCloudType());
    	model.setPluginVersion(getPluginVersion());
    	model.setAuthType(getAuthenticationType());
        model.setName(getName());
        model.setInstanceReadScope(getInstanceReadScope());
        model.setInstanceWriteScope(getInstanceWriteScope());
        model.setStorageReadScope(getStorageReadScope());
        model.setStorageWriteScope(getStorageWriteScope());

        return ResponseEntity.ok(
                Body.create(model));
    }

    /*
     * Abstract Methods
     */

    protected abstract String getCloudType();
    protected abstract String getPluginVersion();
    protected abstract AuthenticationType getAuthenticationType();
    protected abstract String getName();
    protected abstract String getInstanceWriteScope();
    protected abstract String getInstanceReadScope();
    protected abstract String getStorageWriteScope();
    protected abstract String getStorageReadScope();
}
