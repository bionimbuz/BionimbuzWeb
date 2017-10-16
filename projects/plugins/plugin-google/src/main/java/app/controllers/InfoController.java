package app.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import app.common.SystemConstants;
import app.models.InfoModel;

@RestController
public class InfoController {
    
    @RequestMapping(path = "/info")
    public InfoModel info(@RequestParam(value="name", defaultValue="World") String name) {        
        return new InfoModel(
                SystemConstants.SYSTEM_VERSION,
                SystemConstants.CLOUD_TYPE);
    }    
}
