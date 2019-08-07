package app.execution.jobs;

import static app.common.SystemConstants.ARGS_LINE_CMD_REGEX;
import static app.common.SystemConstants.INPUTS_FOLDER;
import static app.common.SystemConstants.INPUT_LINE_CMD_REGEX;
import static app.common.SystemConstants.INPUT_PREFIX;
import static app.common.SystemConstants.OUTPUTS_FOLDER;
import static app.common.SystemConstants.OUTPUT_LINE_CMD_REGEX;
import static app.common.SystemConstants.OUTPUT_PREFIX;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import app.common.LineCmdUtils;
import app.common.Pair;
import app.common.RuntimeUtil;
import app.common.utils.FileUtils;
import app.common.utils.OsUtil;
import app.common.utils.StringUtils;
import app.execution.IApplicationExecution;
import app.models.Command;
import app.models.ExecutionStatus.EXECUTION_PHASE;

public class ExecutorJob {

    protected static final Logger LOGGER = LoggerFactory.getLogger(ExecutorJob.class);
    private static final String APPLICATION_NAME = "application";

    private final Thread job;

    public ExecutorJob(
            final IApplicationExecution executor,
            final Command command) {
        this.job = new Thread(new Core(executor, command));
    }

    public void start() {
        if (Thread.State.NEW != this.job.getState()) {
            return;
        }
        this.job.start();
    }

    private static class Core implements Runnable {

        private final IApplicationExecution executor;
        private final Command command;
        private final List<String> outputFiles = new ArrayList<>();

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
                final File applicationFile = this.generateApplicationFile();
                final String lineCommand = this.generateLineCommand(applicationFile);

                final String processOutput = RuntimeUtil.execAndGetResponseString(new RuntimeUtil.Command(lineCommand));

                if (this.allOutputFilesGenerated()) {
                    this.executor.onSuccess(EXECUTION_PHASE.EXECUTING);
                } else {
                    this.executor.onError(
                            EXECUTION_PHASE.EXECUTING,
                            String.format(
                                    "Some of output files was not generated \n %s",
                                    processOutput));
                }

            } catch (final Exception e) {
                this.executor.onError(EXECUTION_PHASE.EXECUTING, e.getMessage());
            }
        }

        private boolean allOutputFilesGenerated() {
            File file;
            for (final String outputFile : this.outputFiles) {
                file = new File(outputFile);
                if (!file.exists()) {
                    return false;
                }
            }
            return true;
        }

        private static boolean createOutDir() {
            final File dir = new File(OUTPUTS_FOLDER);
            if (dir.exists()) {
                return true;
            }
            return dir.mkdir();
        }

        private File generateApplicationFile() throws IOException {
            if (StringUtils.isEmpty(this.command.getExecutionScript())) {
                return null;
            }
            final File scriptFile = new File(getApplicationFileName());
            try (
                    BufferedWriter writer = new BufferedWriter(new FileWriter(scriptFile))) {
                writer.write(this.command.getExecutionScript());
            }
            if (scriptFile.exists()) {
                FileUtils.setExecutionPermission(scriptFile);
            }
            return scriptFile;
        }

        private String generateLineCommand(final File applicationFile) {
            String lineCommand = this.updateLineCommandWithFiles(
                    this.command.getCommandLine(),
                    INPUTS_FOLDER,
                    INPUT_PREFIX,
                    this.command.getListInputPathsWithExtension(),
                    INPUT_LINE_CMD_REGEX);
            lineCommand = this.updateLineCommandWithFiles(
                    lineCommand,
                    OUTPUTS_FOLDER,
                    OUTPUT_PREFIX,
                    this.command.getListOutputPathsWithExtension(),
                    OUTPUT_LINE_CMD_REGEX);
            lineCommand = this.updateLineCommandArgs(lineCommand);

            if (StringUtils.isEmpty(this.command.getExecutionScript())) {
                return lineCommand;
            }
            return applicationFile.getAbsolutePath() + " " + lineCommand;
        }

        private static String getApplicationFileName() {
            return APPLICATION_NAME + "." + OsUtil.getDefaultScriptExtension();
        }

        private String updateLineCommandArgs(final String lineCommand) {
            String res = lineCommand;
            if (!StringUtils.isEmpty(this.command.getArgs())) {
                res = res.replaceFirst(
                        ARGS_LINE_CMD_REGEX,
                        this.command.getArgs());
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
            for (int i = 0; i < lintFileExtensions.size(); i++) {
                final String extension = lintFileExtensions.get(i).getRight();
                final String fileName = LineCmdUtils.generateFilePath(dir, filePrefix, i, extension);
                if (OUTPUT_PREFIX.equals(filePrefix)) {
                    this.outputFiles.add(fileName);
                }
                res = res.replaceFirst(replacement, fileName);
            }
            res = res.replaceAll(replacement, "");

            return res;
        }
    }
}
