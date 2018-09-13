package app.models;

import java.util.List;

import app.common.Pair;

public class Command extends Body<Command> {

    private String commandLine;
    private String executionScript;
    private String args;
    private String refreshStatusUrl;
    private List<Pair<String, String>> listInputPathsWithExtension;
    private List<Pair<String, String>> listOutputPathsWithExtension;
    private SecureCoordinatorAccess secureFileAccess;

    public Command() {
    }

    public String getCommandLine() {
        return this.commandLine;
    }

    public void setCommandLine(final String commandLine) {
        this.commandLine = commandLine;
    }

    public String getArgs() {
        return this.args;
    }

    public void setArgs(final String args) {
        this.args = args;
    }

    public final SecureCoordinatorAccess getSecureFileAccess() {
        return this.secureFileAccess;
    }

    public final void setSecureFileAccess(final SecureCoordinatorAccess secureFileAccess) {
        this.secureFileAccess = secureFileAccess;
    }

    public final List<Pair<String, String>> getListInputPathsWithExtension() {
        return this.listInputPathsWithExtension;
    }

    public final void setListInputPathsWithExtension(
            final List<Pair<String, String>> listInputPathsWithExtension) {
        this.listInputPathsWithExtension = listInputPathsWithExtension;
    }

    public final List<Pair<String, String>> getListOutputPathsWithExtension() {
        return this.listOutputPathsWithExtension;
    }

    public final void setListOutputPathsWithExtension(
            final List<Pair<String, String>> listOutputPathsWithExtension) {
        this.listOutputPathsWithExtension = listOutputPathsWithExtension;
    }

    public String getExecutionScript() {
        if (this.executionScript != null && !this.executionScript.isEmpty()) {
            this.executionScript = this.executionScript.replace("\r", "");
        }
        return this.executionScript;
    }

    public void setExecutionScript(final String executionScript) {
        this.executionScript = executionScript;
    }

    public String getRefreshStatusUrl() {
        return this.refreshStatusUrl;
    }

    public void setRefreshStatusUrl(final String refreshStatusUrl) {
        this.refreshStatusUrl = refreshStatusUrl;
    }
}
