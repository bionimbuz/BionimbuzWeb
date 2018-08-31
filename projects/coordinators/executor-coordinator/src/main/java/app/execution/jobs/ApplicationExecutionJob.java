package app.execution.jobs;
import static app.common.SystemConstants.INPUTS_FOLDER;
import static app.common.SystemConstants.OUTPUTS_FOLDER;

import app.exceptions.SingletonAlreadyInitializedException;
import app.exceptions.SingletonNotInitializedException;
import app.execution.IApplicationExecution;
import app.models.Command;
import app.models.ExecutionStatus;
import app.models.ExecutionStatus.EXECUTION_PHASE;
import app.models.ExecutionStatus.STATUS;
import app.models.RemoteFileProcessingStatus;

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
        return core.getExecutionStatus().getStatus();
    }
    public ExecutionStatus getExecutionStatus(){
        return core.getExecutionStatus();
    }
    
    /*
     * Thread class
     */

    private static class Core implements Runnable, IApplicationExecution {
        
        private Command command;
        private volatile ExecutionStatus executionStatus;
        private RemoteFileProcessingStatus downloadStatus;
        private RemoteFileProcessingStatus uploadStatus;

        public Core(final Command command) {
            this.command = command;
            this.downloadStatus = new RemoteFileProcessingStatus(
                    command.getListInputPathsWithExtension().size());
            this.uploadStatus = new RemoteFileProcessingStatus(
                    command.getListOutputPathsWithExtension().size());
            this.executionStatus = new ExecutionStatus(
                    downloadStatus, uploadStatus);
        }

        public ExecutionStatus getExecutionStatus() {
            return executionStatus;
        }
        
        @Override
        public void run() {     
            onSuccess(EXECUTION_PHASE.STARTING);   
        }

        @Override
        public void onError(EXECUTION_PHASE phase, String message) {   
            executionStatus.setStatus(STATUS.STOPPED);
            executionStatus.setErrorMessage(message);
            executionStatus.setHasError(true);
            StatusNotifierJob.waitNotification();
        }

        @Override
        public void onSuccess(EXECUTION_PHASE phase) { 
            switch (phase) {
                case STARTING:       
                    executionStatus.setStatus(STATUS.RUNNING);
                    executionStatus.setPhase(EXECUTION_PHASE.DOWNLOADING);
                    StatusNotifierJob.tryNotification();
                    startDownloadPhase();
                    break;
                case DOWNLOADING:
                    executionStatus.setPhase(EXECUTION_PHASE.EXECUTING);
                    StatusNotifierJob.tryNotification();
                    startExecutionPhase();
                    break;
                case EXECUTING:
                    executionStatus.setPhase(EXECUTION_PHASE.UPLOADING);
                    StatusNotifierJob.tryNotification();
                    startUploadPhase();
                    break;
                case UPLOADING:
                    executionStatus.setPhase(EXECUTION_PHASE.FINISHED);
                    executionStatus.setStatus(STATUS.STOPPED);
                    StatusNotifierJob.waitNotification();
                    break;
                default :
                    break;
            }
        }        
        
        private void startDownloadPhase() {
            DownloaderJob job = new DownloaderJob(
                    this,
                    downloadStatus,
                    command.getListInputPathsWithExtension(),
                    INPUTS_FOLDER);
            job.start();
        }        
        
        private void startExecutionPhase() {
            ExecutorJob job = new ExecutorJob(
                    this,
                    command);
            job.start();
        }
        
        private void startUploadPhase() {
            UploaderJob job = new UploaderJob(
                    this,
                    uploadStatus,
                    command.getListOutputPathsWithExtension(),
                    OUTPUTS_FOLDER);
            job.start();
        }
    }

}
