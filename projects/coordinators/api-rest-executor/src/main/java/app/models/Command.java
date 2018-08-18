package app.models;

import java.util.List;

public class Command extends Body<Command> {

    private String commandLine;
    private String workinDir;
    private String args;
    private String extraArgs;
    private List<String> listRemoteFileInputPaths;
    private List<String> listRemoteFileOutputPaths;
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
    public String getExtraArgs() {
        return extraArgs;
    }
    public void setExtraArgs(String extraArgs) {
        this.extraArgs = extraArgs;
    }
    public final List<String> getListRemoteFileInputPaths() {
        return listRemoteFileInputPaths;
    }
    public final void setListRemoteFileInputPaths(
            List<String> listRemoteFileInputPaths) {
        this.listRemoteFileInputPaths = listRemoteFileInputPaths;
    }
    public final List<String> getListRemoteFileOutputPaths() {
        return listRemoteFileOutputPaths;
    }
    public final void setListRemoteFileOutputPaths(
            List<String> listRemoteFileOutputPaths) {
        this.listRemoteFileOutputPaths = listRemoteFileOutputPaths;
    }
    public final String getWorkinDir() {
        return workinDir;
    }
    public final void setWorkinDir(String workinDir) {
        this.workinDir = workinDir;
    }
    public final SecureFileAccess getSecureFileAccess() {
        return secureFileAccess;
    }
    public final void setSecureFileAccess(SecureFileAccess secureFileAccess) {
        this.secureFileAccess = secureFileAccess;
    }
}
