package app.execution;

import static app.common.SystemConstants.INPUTS_FOLDER;

import app.exceptions.SingletonAlreadyInitializedException;
import app.exceptions.SingletonNotInitializedException;
import app.models.Command;
import app.models.ExecutionStatus;
import app.models.ExecutionStatus.EXECUTION_PHASE;
import app.models.RemoteFileProcessingStatus;
import app.models.STATUS;

public class ApplicationExecutionJob {
    
    private static ApplicationExecutionJob inst = null;
    private static Thread job = null;
    private Core core;
    
    /*
     * Singleton Methods
     */
    
    public static void init(final Command command)
            throws SingletonAlreadyInitializedException {
        assertNotInitialized();
        Core core = new Core(command);
        inst = new ApplicationExecutionJob(core);
        job = new Thread(core);
        job.start();
    }
    public static ApplicationExecutionJob get() throws SingletonNotInitializedException {
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
                    ApplicationExecutionJob.class);
        }
    }
    private static void assertInitialized()
            throws SingletonNotInitializedException {
        if(inst == null) {
            throw new SingletonNotInitializedException(
                    ApplicationExecutionJob.class);
        }
    }
    
    /*
     * Constructors
     */
    
    public ApplicationExecutionJob(final Core core) {
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

    private static class Core implements Runnable, IApplicationExecution {
        
        private Command command;
        private STATUS status = STATUS.IDDLE;
        private volatile ExecutionStatus executionStatus;
        private RemoteFileProcessingStatus downloadStatus;
        private RemoteFileProcessingStatus uploadStatus;
        private Object _lock_ = new Object();

        public Core(final Command command) {
            this.command = command;
            this.downloadStatus = new RemoteFileProcessingStatus(
                    command.getListRemoteFileInputPaths().size());
            this.uploadStatus = new RemoteFileProcessingStatus(
                    command.getListRemoteFileOutputPaths().size());
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
            DownloaderJob job = new DownloaderJob(
                    this,
                    downloadStatus,
                    command.getListRemoteFileInputPaths(),
                    INPUTS_FOLDER);
            job.start();
        }        
        
        private void startExecutionPhase() {
            ExecutorJob job = new ExecutorJob(
                    this);
            job.start();
        }
        
        private void startUploadPhase() {
            UploaderJob job = new UploaderJob(
                    this);
            job.start();
        }
    }

}
