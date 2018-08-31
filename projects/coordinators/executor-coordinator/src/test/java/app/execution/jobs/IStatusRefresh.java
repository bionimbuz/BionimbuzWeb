package app.execution.jobs;

import app.models.ExecutionStatus.EXECUTION_PHASE;
import app.models.ExecutionStatus.STATUS;

public interface IStatusRefresh {
    void onStatusRefreshed(STATUS status, EXECUTION_PHASE phase, String errorMessage);
}
