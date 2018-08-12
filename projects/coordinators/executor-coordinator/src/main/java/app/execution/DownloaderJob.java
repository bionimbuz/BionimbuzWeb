package app.execution;

import static app.common.SystemConstants.INPUT_PREFIX;
import static app.common.SystemConstants.MAX_SIMULTANEOUS_DOWNLOADS;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import app.models.ExecutionStatus.EXECUTION_PHASE;
import app.models.RemoteFileInfo;
import app.models.RemoteFileProcessingStatus;

public class DownloaderJob {
    protected static final Logger LOGGER = LoggerFactory.getLogger(DownloaderJob.class);
    
    private Thread job;

    public DownloaderJob(
            final IExecution executor,
            final RemoteFileProcessingStatus downloadStatus,
            List<RemoteFileInfo> lstDownloadFileInfo, 
            String outputDir) {        
        job = new Thread(new Core(
                executor,
                downloadStatus,
                lstDownloadFileInfo,
                outputDir));        
    }
    
    public void start() {
        if(Thread.State.NEW != job.getState()) {
            return;
        }
        job.start();
    }
            
    private interface IDownload {
        void onSuccess(final String fileName);
        void onError(final String fileName, final String message);
    }
    
    private static class Core implements Runnable, IDownload {

        final IExecution executor;
        final RemoteFileProcessingStatus downloadStatus;
        private ExecutorService threadPool;
        private String outputDir;
        private List<Downloader> downloadJobs = new ArrayList<>();        

        public Core(
                final IExecution executor,
                final RemoteFileProcessingStatus downloadStatus,
                List<RemoteFileInfo> lstDownloadFileInfo, 
                String outputDir) {
            this.executor = executor;
            this.downloadStatus = downloadStatus;
            this.outputDir = outputDir;
            this.threadPool = Executors.newFixedThreadPool(MAX_SIMULTANEOUS_DOWNLOADS);
            for(int i = 0; i<lstDownloadFileInfo.size(); i++) {
                RemoteFileInfo fileInfo = lstDownloadFileInfo.get(i);
                this.downloadJobs.add(
                    new Downloader(this, fileInfo, outputDir, INPUT_PREFIX + i));
            }
        }
        
        @Override
        public void run() {
            try {
                createOutDir();
                for (Downloader downloadJob : downloadJobs) {
                    threadPool.execute(downloadJob);
                }
                threadPool.shutdown();
                threadPool.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
                
            } catch (Exception e) {
                executor.onError(
                        EXECUTION_PHASE.DOWNLOADING, 
                        e.getMessage());
            }
        }
        @Override
        public void onSuccess(String fileName) {
            downloadStatus.addSucceded(fileName);
        }
        @Override
        public void onError(String fileName, String message) {     
            downloadStatus.addFailed(fileName, message);       
        }    
       
        private boolean createOutDir() {
            File dir = new File(outputDir);
            if (dir.exists()) {
                return true;
            }
            return dir.mkdir();
        }    
    }
    
    private static class Downloader implements Runnable {

        private IDownload callback;
        private RemoteFileInfo fileInfo;
        private String outputDir;
        private String fileName;

        public Downloader(
                final IDownload callback, 
                final RemoteFileInfo fileInfo, 
                final String outputDir, 
                final String fileName) {
            
            this.callback = callback;
            this.fileInfo = fileInfo;
            this.outputDir = outputDir;
            this.fileName = fileName;
        }

        @Override
        public void run() {
            try {
                LOGGER.info("######### Downloading: " + fileName);
                URL website = new URL(fileInfo.getUrl());                
                HttpURLConnection conn = (HttpURLConnection) website.openConnection();
                conn.setRequestMethod(fileInfo.getMethod().toString());   
                for (Map.Entry<String, String> entry : fileInfo.getHeaders().entrySet()) {
                    conn.setRequestProperty(entry.getKey(), entry.getValue());
                }                
                try(ReadableByteChannel rbc = Channels.newChannel(conn.getInputStream());
                    FileOutputStream fos = new FileOutputStream(new File(outputDir, fileName))) {
                    fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
                }
                this.callback.onSuccess(fileName);
            } catch (IOException e) {
                this.callback.onError(fileName, e.getMessage());
            } catch (Exception e) {
                this.callback.onError(fileName, e.getMessage());
            }
        }
    }
}
