package app.execution.jobs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import app.execution.IApplicationExecution;
import app.models.ExecutionStatus.EXECUTION_PHASE;

public class ExecutorJob {
    protected static final Logger LOGGER = LoggerFactory.getLogger(ExecutorJob.class);
    
    private Thread job;

    public ExecutorJob(
            final IApplicationExecution executor) {        
        job = new Thread(new Core(executor));        
    }
    
    public void start() {
        if(Thread.State.NEW != job.getState()) {
            return;
        }
        job.start();
    }
       
    private static class Core implements Runnable {

        private IApplicationExecution executor;    

        public Core(
                final IApplicationExecution executor) {
            this.executor = executor;
        }
        
        @Override
        public void run() {
            executor.onSuccess(
                    EXECUTION_PHASE.EXECUTING);
        }       
    }    
}
