package app.models;

import java.util.List;

public class Command extends Body {

    private String commandLine;
    private String workinDir;
    private String args;
    private String extraArgs;
    private List<String> listInputs;
    private List<String> listOutputs;

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
    public List<String> getListInputs() {
        return listInputs;
    }
    public void setListInputs(List<String> listInputs) {
        this.listInputs = listInputs;
    }
    public List<String> getListOutputs() {
        return listOutputs;
    }
    public void setListOutputs(List<String> listOutputs) {
        this.listOutputs = listOutputs;
    }
    public final String getWorkinDir() {
        return workinDir;
    }
    public final void setWorkinDir(String workinDir) {
        this.workinDir = workinDir;
    }
}
