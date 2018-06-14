package app.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import app.common.Routes;

@RestController
public class StatusController {
    protected static final Logger LOGGER = LoggerFactory.getLogger(StatusController.class);

    /*
     * Action Methods
     */

    @RequestMapping(path = Routes.STATUS, method = RequestMethod.GET)
    public ResponseEntity< Boolean > getStatus() {
        return ResponseEntity.ok(true);
    }
}
