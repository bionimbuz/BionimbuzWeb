package jobs;

import static app.commons.constants.TimeConstants.SHUTDOWN_TIME;

import java.util.concurrent.TimeUnit;

import app.FaultToleranceModule;
import app.services.BootstrapService;
import play.Logger;
import play.db.jpa.NoTransaction;
import play.jobs.Job;
import play.jobs.OnApplicationStop;

@OnApplicationStop
public class ShutdownJob extends Job {

    @Override
    @NoTransaction
    public void doJob() throws Exception {

        Logger.info("@OnApplicationStop was called...");
        final FaultToleranceModule faultToleranceModule = BootstrapService.getInstance();
        faultToleranceModule.stop();
        TimeUnit.SECONDS.sleep(SHUTDOWN_TIME);
        Logger.info("The FaultToleranceCoordinator was stopped!");
    }
}
