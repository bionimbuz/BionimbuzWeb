package app.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import app.execution.ApplicationExecutionJob;
import app.execution.RemoteFileInfoAccess;
import app.models.Body;
import app.models.Command;
import app.models.ExecutionStatus;
import app.models.STATUS;

@RestController
public class ExecutionController extends AbstractExecutionController {
    protected static final Logger LOGGER = LoggerFactory.getLogger(ExecutionController.class);

    /*
     * Action Methods
     */
    
    @Override
    protected ResponseEntity<Body<Boolean>> startExecution(final Command command)
            throws Exception {
        if(ApplicationExecutionJob.isInitialized()) {
            return ResponseEntity.ok(
                    Body.create(false));
        }
        RemoteFileInfoAccess.init(command.getSecureFileAccess());
        ApplicationExecutionJob.init(command);
        return ResponseEntity.ok(
                Body.create(true));
    }

    @Override
    protected ResponseEntity<Body<ExecutionStatus>> getExecutionStatus()
            throws Exception {
        if(!ApplicationExecutionJob.isInitialized()) {
            return new ResponseEntity<>(
                    Body.create(null),
                    HttpStatus.NOT_FOUND);            
        }
        return ResponseEntity.ok(
                Body.create(ApplicationExecutionJob.get().getExecutionStatus()));
    }

    @Override
    protected ResponseEntity<Body<STATUS>> getStatus()
            throws Exception {
        if(!ApplicationExecutionJob.isInitialized()) {            
            return ResponseEntity.ok(
                    Body.create(STATUS.IDDLE));
        }
        return ResponseEntity.ok(
                Body.create(ApplicationExecutionJob.get().getStatus()));
    }

}
