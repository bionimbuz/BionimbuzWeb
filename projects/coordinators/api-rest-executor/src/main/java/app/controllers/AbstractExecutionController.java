package app.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import app.common.HttpHeadersCustom;
import app.common.Routes;
import app.models.Body;
import app.models.Command;

public abstract class AbstractExecutionController extends BaseControllerVersioned {
    protected static final Logger LOGGER = LoggerFactory.getLogger(AbstractExecutionController.class);

    /*
     * Action Methods
     */

    @RequestMapping(path = Routes.INSTANCES, method = RequestMethod.POST)
    private ResponseEntity<Body<Boolean>> postCommandAction(
            @RequestHeader(value=HttpHeadersCustom.API_VERSION) final String version,
            @RequestBody final Command command
            ) {
        return callImplementedMethod("postCommand", version, command);
    }

    /*
     * Abstract Methods
     */

    protected abstract ResponseEntity<Body<Boolean>> postCommand(
            final String token,
            final Command command) throws Exception;

}
