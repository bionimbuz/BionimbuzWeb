package app.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import app.common.Routes;
import app.common.SystemConstants;
import app.models.InfoModel;

@RestController
public class InfoController {	
    
    @RequestMapping(path = Routes.INFO, method = RequestMethod.GET)
    public ResponseEntity <InfoModel> info() {
    	
    	InfoModel model = new InfoModel(
                SystemConstants.SYSTEM_VERSION,
                SystemConstants.CLOUD_TYPE);        	

        return ResponseEntity
	            .status(HttpStatus.OK)
	            .body(model);
    }    
}
