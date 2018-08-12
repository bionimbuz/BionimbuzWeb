package app.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import app.execution.ExecutorJob;
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
    protected ResponseEntity<Body<Boolean>> startExecution(Command command)
            throws Exception {
        if(ExecutorJob.isInitialized()) {
            return ResponseEntity.ok(
                    Body.create(false));
        }
        ExecutorJob.init(command);
        return ResponseEntity.ok(
                Body.create(true));
    }

    @Override
    protected ResponseEntity<Body<ExecutionStatus>> getExecutionStatus()
            throws Exception {
        if(!ExecutorJob.isInitialized()) {
            return new ResponseEntity<>(
                    Body.create(null),
                    HttpStatus.NOT_FOUND);            
        }
        return ResponseEntity.ok(
                Body.create(ExecutorJob.get().getExecutionStatus()));
    }

    @Override
    protected ResponseEntity<Body<STATUS>> getStatus()
            throws Exception {
        if(!ExecutorJob.isInitialized()) {            
            return ResponseEntity.ok(
                    Body.create(STATUS.IDDLE));
        }
        return ResponseEntity.ok(
                Body.create(ExecutorJob.get().getStatus()));
    }

}
