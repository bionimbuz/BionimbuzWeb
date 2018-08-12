package app.execution;

import static app.common.SystemConstants.INPUTS_FOLDER;

import app.exceptions.SingletonAlreadyInitializedException;
import app.exceptions.SingletonNotInitializedException;
import app.models.Command;
import app.models.ExecutionStatus;
import app.models.ExecutionStatus.EXECUTION_PHASE;
import app.models.RemoteFileProcessingStatus;
import app.models.STATUS;

public class ExecutorJob {
    
    private static ExecutorJob inst = null;
    private static Thread job = null;
    private Core core;
    
    /*
     * Singleton Methods
     */
    
    public static void init(Command command)
            throws SingletonAlreadyInitializedException {
        assertNotInitialized();
        inst = new ExecutorJob(new Core(command));
        job = new Thread(new Core(command));
        job.start();
    }
    public static ExecutorJob get() throws SingletonNotInitializedException {
        assertInitialized();
        return inst;
    }
    public static boolean isInitialized() {
        return inst != null;        
    }
    private static void assertNotInitialized()
            throws SingletonAlreadyInitializedException {
        if(inst != null) {
            throw new SingletonAlreadyInitializedException(
                    ExecutorJob.class);
        }
    }
    private static void assertInitialized()
            throws SingletonNotInitializedException {
        if(inst != null) {
            throw new SingletonNotInitializedException(
                    ExecutorJob.class);
        }
    }
    
    /*
     * Constructors
     */
    
    public ExecutorJob(Core core) {
        this.core = core;
    }    

    /*
     * Methods
     */
    
    public STATUS getStatus(){
        return core.getStatus();
    }
    public ExecutionStatus getExecutionStatus(){
        return core.getExecutionStatus();
    }
    
    /*
     * Thread class
     */

    private static class Core implements Runnable, IExecution {
        
        private Command command;
        private STATUS status = STATUS.IDDLE;
        private ExecutionStatus executionStatus;
        private RemoteFileProcessingStatus downloadStatus;
        private RemoteFileProcessingStatus uploadStatus;
        private Object _lock_ = new Object();

        public Core(Command command) {
            this.command = command;
            this.downloadStatus = new RemoteFileProcessingStatus(
                    command.getListInputs().size());
            this.uploadStatus = new RemoteFileProcessingStatus(
                    command.getListOutputs().size());
            this.executionStatus = new ExecutionStatus(
                    downloadStatus, uploadStatus);
        }

        public ExecutionStatus getExecutionStatus() {
            return executionStatus;
        }
        
        public STATUS getStatus() {
            synchronized (_lock_) {                
                return status;
            }
        }
        public void setStatus(STATUS status) {
            synchronized (_lock_) {
                this.status = status;
            }
        }

        @Override
        public void run() {     
            onSuccess(EXECUTION_PHASE.STARTING);   
        }

        @Override
        public void onError(EXECUTION_PHASE phase, String message) {         
            setStatus(STATUS.STOPPED);
            executionStatus.setMessage(message);
            executionStatus.setHasError(true);
        }

        @Override
        public void onSuccess(EXECUTION_PHASE phase) { 
            switch (phase) {
                case STARTING:       
                    setStatus(STATUS.RUNNING);
                    executionStatus.setPhase(EXECUTION_PHASE.DOWNLOADING);
                    startDownloadPhase();
                    break;
                case DOWNLOADING:
                    executionStatus.setPhase(EXECUTION_PHASE.EXECUTING);
                    startExecutionPhase();
                    break;
                case EXECUTING:
                    executionStatus.setPhase(EXECUTION_PHASE.UPLOADING);
                    startUploadPhase();
                    break;
                case UPLOADING:
                    executionStatus.setPhase(EXECUTION_PHASE.FINISHED);
                    setStatus(STATUS.STOPPED);
                    break;
                default :
                    break;
            }
        }        
        
        private void startDownloadPhase() {
            DownloaderJob downloadJob = new DownloaderJob(
                    this,
                    downloadStatus,
                    command.getListInputs(),
                    INPUTS_FOLDER);
            downloadJob.start();
        }        
        
        private void startExecutionPhase() {
        }
        
        private void startUploadPhase() {
        }
    }

}
