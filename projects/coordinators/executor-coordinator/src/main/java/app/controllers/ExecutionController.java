package app.controllers;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import app.execution.DownloadJob;
import app.models.Body;
import app.models.Command;

@RestController
public class ExecutionController extends AbstractExecutionController {
    protected static final Logger LOGGER = LoggerFactory.getLogger(ExecutionController.class);

    private DownloadJob downloadJob = null;

    /*
     * Action Methods
     */

//    @RequestMapping(path = "/download/status", method = RequestMethod.GET)
//    public ResponseEntity< DownloadStatus > getDownloadStatus() {
//        return ResponseEntity.ok(
//                new DownloadStatus(
//                        downloadJob.hasFinished(),
//                        downloadJob.hasSuccess()));
//    }

    @Override
    protected ResponseEntity<Body<Boolean>> postCommand(String token,
            Command command) throws Exception {

        if(downloadJob != null) {
            return ResponseEntity.ok(
                    Body.create(false));
        }

        List<String> listURLs = new ArrayList<>();
        for(int i = 0; i<20; i++) {
            listURLs.add("https://code.jquery.com/jquery-3.3.1.js");
        }
        downloadJob = new DownloadJob(listURLs, "inputs");
        downloadJob.start();
        return ResponseEntity.ok(
                Body.create(true));
    }

}
