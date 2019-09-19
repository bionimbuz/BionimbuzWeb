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
    private final Core core;

    /*
     * Singleton Methods
     */

    public static void init(final Command command)
            throws SingletonAlreadyInitializedException {
        assertNotInitialized();
        final Core core = new Core(command);
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
        return this.core.getExecutionStatus().getStatus();
    }
    public ExecutionStatus getExecutionStatus(){
        return this.core.getExecutionStatus();
    }

    /*
     * Thread class
     */

    private static class Core implements Runnable, IApplicationExecution {

        private final Command command;
        private volatile ExecutionStatus executionStatus;
        private final RemoteFileProcessingStatus downloadStatus;
        private final RemoteFileProcessingStatus uploadStatus;

        public Core(final Command command) {
            this.command = command;
            this.downloadStatus = new RemoteFileProcessingStatus(command.getListInputPathsWithExtension().size());
            this.uploadStatus = new RemoteFileProcessingStatus(command.getListOutputPathsWithExtension().size());
            this.executionStatus = new ExecutionStatus(this.downloadStatus, this.uploadStatus);
        }

        public ExecutionStatus getExecutionStatus() {
            return this.executionStatus;
        }

        @Override
        public void run() {
            this.onSuccess(EXECUTION_PHASE.STARTING);
        }

        @Override
        public void onError(final EXECUTION_PHASE phase, final String message) {
            this.executionStatus.setStatus(STATUS.STOPPED);
            this.executionStatus.setErrorMessage(message);
            this.executionStatus.setHasError(true);
            StatusNotifierJob.waitNotification();
        }

        @Override
        public void onSuccess(final EXECUTION_PHASE phase) {
            switch (phase) {
                case STARTING:
                    this.executionStatus.setStatus(STATUS.RUNNING);
                    this.executionStatus.setPhase(EXECUTION_PHASE.DOWNLOADING);
                    StatusNotifierJob.tryNotification();
                    this.startDownloadPhase();
                    break;
                case DOWNLOADING:
                    this.executionStatus.setPhase(EXECUTION_PHASE.EXECUTING);
                    StatusNotifierJob.tryNotification();
                    this.startExecutionPhase();
                    break;
                case EXECUTING:
                    this.executionStatus.setPhase(EXECUTION_PHASE.UPLOADING);
                    StatusNotifierJob.tryNotification();
                    this.startUploadPhase();
                    break;
                case UPLOADING:
                    this.executionStatus.setPhase(EXECUTION_PHASE.FINISHED);
                    this.executionStatus.setStatus(STATUS.STOPPED);
                    StatusNotifierJob.waitNotification();
                    break;
                default :
                    break;
            }
        }

        private void startDownloadPhase() {
            final DownloaderJob job = new DownloaderJob(
                    this,
                    this.downloadStatus,
                    this.command.getListInputPathsWithExtension(),
                    INPUTS_FOLDER);
            job.start();
        }

        private void startExecutionPhase() {
            final ExecutorJob job = new ExecutorJob(
                    this,
                    this.command);
            job.start();
        }

        private void startUploadPhase() {
            final UploaderJob job = new UploaderJob(
                    this,
                    this.uploadStatus,
                    this.command.getListOutputPathsWithExtension(),
                    OUTPUTS_FOLDER);
            job.start();
        }
    }

}
