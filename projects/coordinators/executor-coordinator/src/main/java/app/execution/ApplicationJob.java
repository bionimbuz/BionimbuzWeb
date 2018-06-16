package app.execution;

import app.exceptions.SingletonAlreadyInitializedException;
import app.exceptions.SingletonNotInitializedException;
import app.models.Command;

public class ApplicationJob{

    public static enum Phase{
        PHASE_STARTING,
        PHASE_DOWNLOAD,
        PHASE_EXECUTION,
        PHASE_UPLOAD,
        PHASE_FINISHED
    }

    private static ApplicationJob inst = null;
    private static Thread job = null;

    public static void init(Command command)
            throws SingletonAlreadyInitializedException {
        assertNotInitialized();
        inst = new ApplicationJob();
        job = new Thread(new Core(command));
        job.start();
    }

    private static void assertNotInitialized()
            throws SingletonAlreadyInitializedException {
        if(inst != null) {
            throw new SingletonAlreadyInitializedException(ApplicationJob.class);
        }
    }

    private static void assertInitialized()
            throws SingletonNotInitializedException {
        if(inst != null) {
            throw new SingletonNotInitializedException(ApplicationJob.class);
        }
    }

    private static class Core implements Runnable{
        private Command command;

        public Core(Command command) {
            this.command = command;
        }

        public Command getCommand() {
            return command;
        }

        @Override
        public void run() {
            int i = 0;
            while(true) {
                System.out.println(++i + " ######################################");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
