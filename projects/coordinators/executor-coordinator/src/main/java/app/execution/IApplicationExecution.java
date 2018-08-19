package app.execution;

import app.models.ExecutionStatus.EXECUTION_PHASE;

public interface IApplicationExecution {
    void onError(final EXECUTION_PHASE phase, final String message);
    void onSuccess(final EXECUTION_PHASE phase);
}
