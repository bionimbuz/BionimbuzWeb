package app.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import app.common.Response;
import app.common.Routes;
import app.common.SystemConstants;
import app.models.InfoModel;

@RestController
public class InfoController {	
    
    @RequestMapping(path = Routes.INFO, method = RequestMethod.GET)
    public Response <InfoModel> info() {
    	InfoModel model = new InfoModel(
                SystemConstants.SYSTEM_VERSION,
                SystemConstants.CLOUD_TYPE);    	
    	Response<InfoModel> response = Response.success(model);
        return response;
    }    
}
