package app.execution;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import app.models.ExecutionStatus.EXECUTION_PHASE;

public class UploaderJob {
    protected static final Logger LOGGER = LoggerFactory.getLogger(UploaderJob.class);
    
    private Thread job;

    public UploaderJob(
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
                    EXECUTION_PHASE.UPLOADING);
        }
       
    }
    
}
