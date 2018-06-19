package app.controllers;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import app.common.Routes;
import app.execution.DownloadJob;

@RestController
public class ExecutionController {
    protected static final Logger LOGGER = LoggerFactory.getLogger(ExecutionController.class);

    private DownloadJob downloadJob = null;

    /*
     * Action Methods
     */

    @RequestMapping(path = Routes.EXECUTION_START, method = RequestMethod.GET)
    public ResponseEntity< Boolean > startExecution(
//            @RequestBody Command command
            ) {
        if(downloadJob != null)
            return ResponseEntity.ok(false);

        List<String> listURLs = new ArrayList<>();
        for(int i = 0; i<20; i++) {
            listURLs.add("https://code.jquery.com/jquery-3.3.1.js");
        }
        downloadJob = new DownloadJob(listURLs, "inputs");
        downloadJob.start();
        return ResponseEntity.ok(true);
    }

    @RequestMapping(path = "/download/status", method = RequestMethod.GET)
    public ResponseEntity< DownloadStatus > getDownloadStatus() {
        return ResponseEntity.ok(
                new DownloadStatus(
                        downloadJob.hasFinished(),
                        downloadJob.hasSuccess()));
    }

    public static class DownloadStatus{

        private boolean finished;
        private boolean success;

        public DownloadStatus() {
        }

        public DownloadStatus(boolean finished, boolean success) {
            this.finished = finished;
            this.success = success;
        }

        public boolean isFinished() {
            return finished;
        }

        public void setFinished(boolean finished) {
            this.finished = finished;
        }

        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }




    }



}
