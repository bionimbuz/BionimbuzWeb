package app.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import app.common.GlobalConstants;
import app.common.Routes;
import app.models.InfoModel;

public abstract class AbstractInfoController extends BaseController {	    
    protected static final Logger LOGGER = LoggerFactory.getLogger(AbstractInfoController.class);  
    
    /*
     * Action Methods
     */
    
    @RequestMapping(path = Routes.INFO, method = RequestMethod.GET)
    private ResponseEntity <InfoModel> getInfoAction() {
    	
    	InfoModel model = new InfoModel(GlobalConstants.API_VERSION);
    	model.setCloudType(getCloudType());
    	model.setPluginVersion(getPluginVersion());

        return ResponseEntity
	            .status(HttpStatus.OK)
	            .body(model);
    }    
    
    /*
     * Abstract Methods
     */
    
    protected abstract String getCloudType();
    protected abstract String getPluginVersion();
}
