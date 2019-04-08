package app.execution.jobs;

import static app.common.SystemConstants.MAX_SIMULTANEOUS_DOWNLOADS;
import static app.common.SystemConstants.OUTPUT_PREFIX;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import app.common.Pair;
import app.common.SystemConstants;
import app.common.utils.StringUtils;
import app.execution.CoordinatorServerAccess;
import app.execution.IApplicationExecution;
import app.models.ExecutionStatus.EXECUTION_PHASE;
import app.models.RemoteFileInfo;
import app.models.RemoteFileProcessingStatus;

public class UploaderJob {
    protected static final Logger LOGGER = LoggerFactory.getLogger(UploaderJob.class);
    
    private Thread job;

    public UploaderJob(
            final IApplicationExecution executor,
            final RemoteFileProcessingStatus uploadStatus,
            final List<Pair<String, String>> listRemoteFilePathsWithExtension,
            final String inputDir) {        
        job = new Thread(new Core(
                executor,
                uploadStatus,
                listRemoteFilePathsWithExtension,
                inputDir));        
    }
    
    public void start() {
        if(Thread.State.NEW != job.getState()) {
            return;
        }
        job.start();
    }
            
    private interface IUpload {
        void onSuccess(final String fileName);
        void onError(final String fileName, final String message);
    }
    
    private static class Core implements Runnable, IUpload {

        private IApplicationExecution executor;
        private RemoteFileProcessingStatus uploadStatus;
        private ExecutorService threadPool;
        private List<Uploader> uploaderJobs = new ArrayList<>();     

        public Core(
                final IApplicationExecution executor,
                final RemoteFileProcessingStatus uploadStatus,
                final List<Pair<String, String>>  listRemoteFilePathsWithExtension,
                final String inputDir) {
            this.executor = executor;
            this.uploadStatus = uploadStatus;
            this.threadPool = Executors.newFixedThreadPool(MAX_SIMULTANEOUS_DOWNLOADS);
            
            for(int i = 0; i<listRemoteFilePathsWithExtension.size(); i++) {
                Pair<String, String> fileWithExtension = listRemoteFilePathsWithExtension.get(i);
                this.uploaderJobs.add(
                    new Uploader(this, fileWithExtension, inputDir, OUTPUT_PREFIX + i));
            }
        }
        
        @Override
        public void run() {
            try {
                for (Uploader uploaderJob : uploaderJobs) {
                    threadPool.submit(uploaderJob);
                }
                threadPool.shutdown();
                threadPool.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
                if(uploadStatus.getFailed().size() == 0) {
                    executor.onSuccess(
                            EXECUTION_PHASE.UPLOADING);
                }
            } catch (Exception e) {
                executor.onError(
                        EXECUTION_PHASE.UPLOADING, 
                        e.getMessage());
            }
        }
        @Override
        public void onSuccess(String fileName) {
            uploadStatus.addSucceded(fileName);
        }
        @Override
        public void onError(String fileName, String message) {     
            uploadStatus.addFailed(fileName, message);       
        }           
    }
    
    private static class Uploader implements Runnable {

        private IUpload callback;
        private String filePath;
        private String inputDir;
        private String fileName;

        public Uploader(
                final IUpload callback, 
                final Pair<String, String> filePathWithExtension,
                final String inputDir, 
                final String fileName) {
            
            this.callback = callback;
            this.filePath = filePathWithExtension.getLeft();
            this.inputDir = inputDir;
            this.fileName = fileName;
            String extension = filePathWithExtension.getRight();
            if(!StringUtils.isEmpty(extension)){
                if(extension.charAt(0) != SystemConstants.DOT) {
                    this.fileName += SystemConstants.DOT;
                }
                this.fileName += filePathWithExtension.getRight();
            }
        }

        @Override
        public void run() {
            try {
                LOGGER.info("######### Uploading: " + fileName);
                RemoteFileInfo fileInfo =
                        CoordinatorServerAccess.get().getRemoteFileInfo(filePath);  
                File file = new File(inputDir, fileName);                
                HttpHeaders headers = new HttpHeaders();
                if(fileInfo.getHeaders() != null) {
                    for (Map.Entry<String, String> entry : fileInfo.getHeaders().entrySet()) {
                        headers.add(entry.getKey(), entry.getValue());
                    }                
                }                
                HttpEntity<FileSystemResource> requestEntity =
                        new HttpEntity<>(new FileSystemResource(file), headers);
                
                RestTemplate restTemplate = new RestTemplate();      
                
                SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
                requestFactory.setBufferRequestBody(false);
                restTemplate.setRequestFactory(requestFactory);
                
                ResponseEntity<Object> response =
                        restTemplate.exchange(
                                fileInfo.getUrl(),
                                HttpMethod.valueOf(fileInfo.getMethod()),
                                requestEntity,
                                new ParameterizedTypeReference<Object>() {});                
                this.callback.onSuccess(fileName);
            } catch (Exception e) {
                this.callback.onError(fileName, e.getMessage());
            }
        }
    }
}
