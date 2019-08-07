package app.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import app.execution.CoordinatorServerAccess;
import app.execution.jobs.ApplicationExecutionJob;
import app.models.Body;
import app.models.Command;
import app.models.ExecutionStatus;
import app.models.ExecutionStatus.STATUS;

@RestController
public class ExecutionController extends AbstractExecutionController {

    /*
     * Action Methods
     */

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // * @see app.controllers.AbstractExecutionController#startExecution(app.models.Command)
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    @Override
    protected ResponseEntity<Body<Boolean>> startExecution(final Command command)  throws Exception {

        if (ApplicationExecutionJob.isInitialized()) {
            return ResponseEntity.ok(Body.create(false));
        }
        CoordinatorServerAccess.init(
                command.getRefreshStatusUrl(),
                command.getSecureFileAccess());

        ApplicationExecutionJob.init(command);
        return ResponseEntity.ok(Body.create(true));
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // * @see app.controllers.AbstractExecutionController#getExecutionStatus()
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    @Override
    protected ResponseEntity<Body<ExecutionStatus>> getExecutionStatus()
            throws Exception {
        if (!ApplicationExecutionJob.isInitialized()) {
            return new ResponseEntity<>(
                    Body.create(null),
                    HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(
                Body.create(ApplicationExecutionJob.get().getExecutionStatus()));
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // * @see app.controllers.AbstractExecutionController#getStatus()
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    @Override
    protected ResponseEntity<Body<STATUS>> getStatus()
            throws Exception {
        if (!ApplicationExecutionJob.isInitialized()) {
            return ResponseEntity.ok(
                    Body.create(STATUS.IDDLE));
        }
        return ResponseEntity.ok(
                Body.create(ApplicationExecutionJob.get().getStatus()));
    }
}