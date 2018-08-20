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
import java.util.ArrayList;
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
        private List<String> outputFiles = new ArrayList<>();
        
        public Core(
                final IApplicationExecution executor,
                final Command command) {
            this.executor = executor;
            this.command = command;
        }
                
        @Override
        public void run() {            
            try {
                createOutDir();
                String lineCommand = generateLinecommand();                
                Process process = 
                        Runtime.getRuntime().exec(lineCommand);
                
                if(process.waitFor() == 0) {
                    if(allOutputFilesGenerated()) {
                        executor.onSuccess(
                                EXECUTION_PHASE.EXECUTING);                        
                    } else {
                        executor.onError(
                                EXECUTION_PHASE.EXECUTING, "Some of output files was not generated");
                    }
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

        private boolean createOutDir() {
            File dir = new File(OUTPUTS_FOLDER);
            if (dir.exists()) {
                return true;
            }
            return dir.mkdir();
        }    

        private boolean allOutputFilesGenerated() {
            File file;            
            for (String outputFile : outputFiles) {
                file = new File(outputFile);
                if(!file.exists()) 
                    return false;
            }
            return true;
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
            if(!StringUtils.isEmpty(command.getArgs())){
                res = res.replaceFirst(
                        ARGS_LINE_CMD_REGEX, 
                        command.getArgs());
            }            
            res = res.replaceAll(
                    ARGS_LINE_CMD_REGEX, 
                    "");
            return lineCommand;
        }     
        
        private String generateFilePath(
                final String dir, 
                final String prefix,
                final int id,
                final String extension) {
            StringBuilder fileName = new StringBuilder();
            fileName.append(dir);
            fileName.append(prefix);
            fileName.append(id);      
            if(!StringUtils.isEmpty(extension)){
                if(extension.charAt(0) != SystemConstants.DOT) {
                    fileName.append(SystemConstants.DOT);
                }
                fileName.append(extension);
            } 
            return fileName.toString();
        }
        
        private String updateLineCommandWithFiles(
                final String lineCommand,
                final String dir,
                final String filePrefix,
                final List<Pair<String, String>> lintFileExtensions,
                final String replacement) {   
            String res = lineCommand;          
            for(int i=0; i<lintFileExtensions.size(); i++) {          
                String extension = lintFileExtensions.get(i).getRight();                
                String fileName = generateFilePath(dir, filePrefix, i, extension);    
                outputFiles.add(fileName);
                res = res.replaceFirst(replacement, fileName);
            }
            res = res.replaceAll(replacement, "");
            
            return res;
        }        
    }    
}
