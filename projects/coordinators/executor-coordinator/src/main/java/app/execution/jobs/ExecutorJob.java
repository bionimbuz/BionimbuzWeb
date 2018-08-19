package app.execution.jobs;

import static app.common.SystemConstants.ARGS_LINE_CMD_REGEX;
import static app.common.SystemConstants.INPUTS_FOLDER;
import static app.common.SystemConstants.INPUT_LINE_CMD_REGEX;
import static app.common.SystemConstants.INPUT_PREFIX;
import static app.common.SystemConstants.OUTPUTS_FOLDER;
import static app.common.SystemConstants.OUTPUT_LINE_CMD_REGEX;
import static app.common.SystemConstants.OUTPUT_PREFIX;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import app.common.Pair;
import app.common.SystemConstants;
import app.common.utils.StringUtils;
import app.execution.IApplicationExecution;
import app.models.Command;
import app.models.ExecutionStatus.EXECUTION_PHASE;

public class ExecutorJob {
    protected static final Logger LOGGER = LoggerFactory.getLogger(ExecutorJob.class);
    
    private Thread job;

    public ExecutorJob(
            final IApplicationExecution executor,
            final Command command) {        
        job = new Thread(new Core(executor, command));        
    }
    
    public void start() {
        if(Thread.State.NEW != job.getState()) {
            return;
        }
        job.start();
    }
       
    private static class Core implements Runnable {

        private IApplicationExecution executor;   
        private Command command;

        public Core(
                final IApplicationExecution executor,
                final Command command) {
            this.executor = executor;
            this.command = command;
        }
        
        @Override
        public void run() {            
            try {
                String lineCommand = generateLinecommand();                
                Process process = Runtime.getRuntime().exec(
                        lineCommand,
                        null,
                        new File(command.getWorkinDir()));
                
                if(process.waitFor() == 0) {
                    executor.onSuccess(
                            EXECUTION_PHASE.EXECUTING);
                    return;                    
                }
                
                try(ByteArrayOutputStream output = new ByteArrayOutputStream()) {
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = process.getErrorStream().read(buffer)) != -1) {
                        output.write(buffer, 0, length);
                    }
                    String error = output.toString(StandardCharsets.UTF_8.name());
                    executor.onError(
                            EXECUTION_PHASE.EXECUTING, error);
                }
                
            } catch(Exception e) {
                executor.onError(
                        EXECUTION_PHASE.EXECUTING, e.getMessage());
            }            
        }

        private String generateLinecommand() {
            String lineCommand = 
                    updateLineCommandWithFiles(
                            command.getCommandLine(),
                            INPUTS_FOLDER,
                            INPUT_PREFIX,
                            command.getListInputPathsWithExtension(),
                            INPUT_LINE_CMD_REGEX);
            lineCommand = 
                    updateLineCommandWithFiles(
                            lineCommand,
                            OUTPUTS_FOLDER,
                            OUTPUT_PREFIX,
                            command.getListOutputPathsWithExtension(),
                            OUTPUT_LINE_CMD_REGEX);
            lineCommand = 
                    updateLineCommandArgs(lineCommand);
            return lineCommand;
        }

        private String updateLineCommandArgs(String lineCommand) {
            String res = lineCommand;
            if(!StringUtils.isEmpty(command.getExtraArgs())){
                res = res.replaceFirst(
                        ARGS_LINE_CMD_REGEX, 
                        command.getExtraArgs());
            }            
            res = res.replaceAll(
                    ARGS_LINE_CMD_REGEX, 
                    "");
            return lineCommand;
        }     
        
        private String updateLineCommandWithFiles(
                final String lineCommand,
                final String dir,
                final String filePrefix,
                final List<Pair<String, String>> lintFileExtensions,
                final String replacement) {   
            String res = lineCommand;
            StringBuilder fileName;            
            for(int i=0; i<lintFileExtensions.size(); i++) {
                fileName = new StringBuilder();
                fileName.append(dir);
                fileName.append(filePrefix);
                fileName.append(i);                
                String extension = lintFileExtensions.get(i).getRight();
                if(!StringUtils.isEmpty(extension)){
                    if(extension.charAt(0) != SystemConstants.DOT) {
                        fileName.append(SystemConstants.DOT);
                    }
                    fileName.append(extension);
                }               
                res = res.replaceFirst(replacement, fileName.toString());
            }
            res = res.replaceAll(replacement, "");
            
            return res;
        }        
    }    
}
