package app;

import static app.commons.constants.TimeConstants.DEFAULT_TIME_UNIT;

import java.lang.management.ManagementFactory;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.EnableScheduling;

import app.commons.exceptions.SystemException;
import app.commons.utils.LoggerUtil;
import app.models.AttemptsNumber;
import app.models.Level;
import app.models.Retry;
import app.models.SoftwareRejuvenation;
import app.models.Timeout;
import app.services.BootstrapService;

@SpringBootApplication
@EnableScheduling
public class ApplicationGCP {

    private static final Instant START = Instant.now();
    private static final long HEARTBEAT_TIME = 3L;
    private static final String APPLICATION_STARTUP_TIME = "Application startup time: ";
    private static final String RUNTIME_MODULE_ID = ManagementFactory.getRuntimeMXBean().getName();
    private static final String FAILED_TO_START_FAULT_TOLERANCE_MODULE = "Failed to start FaultToleranceModule!";
    private static final String STARTUP_COMMAND = "java -jar /home/plugin-gcp-0.1.jar";
    private static final String STARTUP_COORDINATOR_COMMAND = "java -jar /home/ft-coordinator-0.0.1-exec.jar --server.port=7777";
    //    private static final String STARTUP_COMMAND = "java -jar /Users/jgomes/developer/projects/aux-projects/BionimbuzWeb/projects/plugins/plugin-gcp/target/plugin-gcp-0.1.jar";
    //    private static final String STARTUP_COORDINATOR_COMMAND = "java -jar /Users/jgomes/developer/projects/aux-projects/BionimbuzWeb/projects/web/conf/fault-tolerance/ft-coordinator-0.0.1-exec.jar --server.port=7777";
    //    private static final String STARTUP_COMMAND = "java -jar C:/Users/jeffe/developer/projects/java/BionimbuzWeb/projects/plugins/plugin-gcp/target/plugin-gcp-0.1.jar";
    //    private static final String STARTUP_COORDINATOR_COMMAND = "java -jar C:/Users/jeffe/developer/projects/java/BionimbuzWeb/projects/web/conf/fault-tolerance/ft-coordinator-0.0.1-exec.jar --server.port=7777";

    public static void main(final String[] args) {
        SpringApplication.run(ApplicationGCP.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public static void init() {

        startFaultToleranceModule();
        LoggerUtil.info(APPLICATION_STARTUP_TIME + Duration.between(START, Instant.now()).toMillis());
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
