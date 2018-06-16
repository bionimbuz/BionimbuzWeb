package app.execution;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DownloadJob {
    protected static final Logger LOGGER = LoggerFactory.getLogger(DownloadJob.class);

    private static final int MAX_SIMULTANEOUS_DOWNLOADS = 4;
    private static final String PREFIX = "f";
    private ExecutorService executor;
    private String outputDir;
    private List<Core> downloadJobs = new ArrayList<>();

    public DownloadJob(List<String> fileURLs, String outputDir) {
        this.outputDir = outputDir;
        this.executor = Executors.newFixedThreadPool(MAX_SIMULTANEOUS_DOWNLOADS);
        for(int i = 0; i<fileURLs.size(); i++) {
            String fileURL = fileURLs.get(i);
            this.downloadJobs.add(
                new Core(fileURL, outputDir, PREFIX + i));
        }
    }

    public void start() {
        createOutDir();
        for (Core downloadJob : downloadJobs) {
            executor.execute(downloadJob);
        }
        executor.shutdown();
    }

    private boolean createOutDir() {
        File theDir = new File(outputDir);
        if (theDir.exists()) {
            return true;
        }
        return theDir.mkdir();
    }

    public boolean hasFinished() {
        return executor.isTerminated();
    }

    public boolean hasSuccess() {
        if(!hasFinished())
            return false;
        for (Core downloadJob : downloadJobs) {
            if(!downloadJob.hasSuccess())
                return false;
        }
        return true;
    }

    private static class Core implements Runnable{

        private String fileURL;
        private String outputDir;
        private String fileName;
        private boolean success = false;
        private String errorMessage = "";

        public Core(final String fileUrl, final String outputDir, final String fileName) {
            this.fileURL = fileUrl;
            this.outputDir = outputDir;
            this.fileName = fileName;
        }

        @Override
        public void run() {
            try {
                LOGGER.info("######### Downloading: " + fileName);
                URL website = new URL(fileURL);
                try(ReadableByteChannel rbc = Channels.newChannel(website.openStream());
                    FileOutputStream fos = new FileOutputStream(new File(outputDir, fileName))) {
                    fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
                }
                setSuccess(true);
            } catch (IOException e) {
                setSuccess(false);
                setErrorMessage(e.getMessage());
            }
        }

        private synchronized void setSuccess(boolean success) {
            this.success = success;
        }
        private synchronized boolean hasSuccess() {
            return success;
        }
        public synchronized String getErrorMessage() {
            return errorMessage;
        }
        public synchronized void setErrorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
        }
    }
}
