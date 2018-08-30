package app.models;

import java.util.List;

import app.common.Pair;

public class Command extends Body<Command> {

    private String commandLine;
    private String executionScript;
    private String args;
    private List<Pair<String, String>> listInputPathsWithExtension;
    private List<Pair<String, String>> listOutputPathsWithExtension;
    private SecureFileAccess secureFileAccess;
    
    public Command() {
    }

    public String getCommandLine() {
        return commandLine;
    }
    public void setCommandLine(String commandLine) {
        this.commandLine = commandLine;
    }
    public String getArgs() {
        return args;
    }
    public void setArgs(String args) {
        this.args = args;
    }
    public final SecureFileAccess getSecureFileAccess() {
        return secureFileAccess;
    }
    public final void setSecureFileAccess(SecureFileAccess secureFileAccess) {
        this.secureFileAccess = secureFileAccess;
    }
    public final List<Pair<String, String>> getListInputPathsWithExtension() {
        return listInputPathsWithExtension;
    }
    public final void setListInputPathsWithExtension(
            List<Pair<String, String>> listInputPathsWithExtension) {
        this.listInputPathsWithExtension = listInputPathsWithExtension;
    }
    public final List<Pair<String, String>> getListOutputPathsWithExtension() {
        return listOutputPathsWithExtension;
    }
    public final void setListOutputPathsWithExtension(
            List<Pair<String, String>> listOutputPathsWithExtension) {
        this.listOutputPathsWithExtension = listOutputPathsWithExtension;
    }
    public String getExecutionScript() {
        return executionScript;
    }
    public void setExecutionScript(String executionScript) {
        this.executionScript = executionScript;
    }
}
