package app.controllers;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import app.common.Routes;
import app.execution.DownloadJob;
import app.models.Command;
import app.models.DownloadStatus;

@RestController
public class ExecutionController {
    protected static final Logger LOGGER = LoggerFactory.getLogger(ExecutionController.class);

    private DownloadJob downloadJob = null;

    /*
     * Action Methods
     */

    @RequestMapping(path = Routes.EXECUTION_START, method = RequestMethod.GET)
    public ResponseEntity< Boolean > startExecution(
            @RequestBody Command command) {

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

}
