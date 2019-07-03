package app.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import app.common.HttpHeadersCustom;
import app.common.Routes;
import app.models.Body;
import app.models.Command;
import app.models.ExecutionStatus;
import app.models.ExecutionStatus.STATUS;

public abstract class AbstractExecutionController extends BaseControllerVersioned {

    /*
     * Action Methods
     */

    @RequestMapping(path = Routes.EXECUTION_START, method = RequestMethod.POST)
    private ResponseEntity<Body<Boolean>> startExecutionAction(
            @RequestHeader(value = HttpHeadersCustom.API_VERSION) final String version,
            @RequestBody final Command command) {
        return this.callImplementedMethod("startExecution", version, command);
    }

    @RequestMapping(path = Routes.EXECUTION_STATUS, method = RequestMethod.GET)
    private ResponseEntity<Body<ExecutionStatus>> getExecutionStatusAction(
            @RequestHeader(value = HttpHeadersCustom.API_VERSION) final String version) {
        return this.callImplementedMethod("getExecutionStatus", version);
    }

    @RequestMapping(path = Routes.STATUS, method = RequestMethod.GET)
    private ResponseEntity<Body<STATUS>> getStatusAction(
            @RequestHeader(value = HttpHeadersCustom.API_VERSION) final String version) {
        return this.callImplementedMethod("getStatus", version);
    }

    /*
     * Abstract Methods
     */

    protected abstract ResponseEntity<Body<Boolean>> startExecution(
            final Command command) throws Exception;

    protected abstract ResponseEntity<Body<ExecutionStatus>> getExecutionStatus()
            throws Exception;

    protected abstract ResponseEntity<Body<STATUS>> getStatus()
            throws Exception;

}
