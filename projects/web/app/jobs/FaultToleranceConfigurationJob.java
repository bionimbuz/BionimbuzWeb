package jobs;

import static app.commons.constants.TimeConstants.DEFAULT_TIME_UNIT;

import java.lang.management.ManagementFactory;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import app.FaultToleranceModule;
import app.commons.exceptions.SystemException;
import app.commons.utils.LoggerUtil;
import app.models.AttemptsNumber;
import app.models.Level;
import app.models.Retry;
import app.models.SoftwareRejuvenation;
import app.models.Timeout;
import app.services.BootstrapService;
import play.jobs.Job;
import play.jobs.OnApplicationStart;

@OnApplicationStart
public class FaultToleranceConfigurationJob extends Job {

    private static final String RUNTIME_MODULE_ID = ManagementFactory.getRuntimeMXBean().getName();
    private final static String STARTUP_COMMAND = "/home/play-1.4.4/play-1.4.4 start " + System.getProperty("user.dir");
    //    private final static String STARTUP_COMMAND = "/Volumes/Data/developer/servers/play-1.4.4/play-1.4.4 start " + System.getProperty("user.dir");
    //    private static final String STARTUP_COMMAND = "java -jar C:/Users/jeffe/developer/servers/play-1.4.4/play-1.4.4 start " + System.getProperty("user.dir");
    private static final String STARTUP_COORDINATOR_COMMAND = "java -jar ./conf/fault-tolerance/ft-coordinator-0.0.1-exec.jar --server.port=7775";
    private static final String FAILED_TO_START_FAULT_TOLERANCE_MODULE = "Failed to start FaultToleranceModule!";
    private static final long HEARTBEAT_TIME = 1L;

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // * @see play.jobs.Job#doJob()
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    @Override
    public void doJob() {

        startFaultToleranceModule();
    }

    private static void startFaultToleranceModule() {

        CompletableFuture.runAsync(() -> {
            try {
                final FaultToleranceModule faultToleranceModule = BootstrapService.getInstance();
                final Level faultToleranceLevel = new Level(STARTUP_COMMAND, HEARTBEAT_TIME);
                faultToleranceLevel.setModuleId(RUNTIME_MODULE_ID);
                faultToleranceLevel.addTechnique(bindSoftwareRejuvenation());
                faultToleranceLevel.addTechnique(bindRetry());
                faultToleranceModule.start(faultToleranceLevel, STARTUP_COORDINATOR_COMMAND);
            } catch (SystemException | InterruptedException e) {
                LoggerUtil.error(FAILED_TO_START_FAULT_TOLERANCE_MODULE, e);
            }
        });
    }

    private static SoftwareRejuvenation bindSoftwareRejuvenation() {

        final Long rejuvanationTimeout = 60 * 60 * 24L; // 24 HOURS
        final int maxAllowedCpuUsage = 100;
        final int maxAllowedMemoryUsage = 100;
        final Timeout timeout = new Timeout(rejuvanationTimeout, DEFAULT_TIME_UNIT);
        return new SoftwareRejuvenation(timeout, maxAllowedCpuUsage, maxAllowedMemoryUsage);
    }

    private static Retry bindRetry() {

        final Timeout timeout = new Timeout(1L, DEFAULT_TIME_UNIT);
        return new Retry(timeout, new AttemptsNumber(1L));
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Inner classes to fault injection
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    public static class FaultInjectionMTBFThread extends Thread {

        // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        // * @see java.lang.Thread#run()
        // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        @Override
        public void run() {

            try {
                LoggerUtil.warn("Waiting to fault injection");
                TimeUnit.MINUTES.sleep(5);
                LoggerUtil.warn("Doing fault injection");
                System.exit(1);
            } catch (final InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}