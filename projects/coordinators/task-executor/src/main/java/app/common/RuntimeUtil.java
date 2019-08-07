package app.common;

import static app.common.SystemConstants.LINE_BREAK;
import static app.common.SystemConstants.SPACE_STRING;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class RuntimeUtil {

    // ---------------------------------------------------------------------------------------------
    // Constructors.
    // ---------------------------------------------------------------------------------------------
    private RuntimeUtil() {
        super();
    }

    public static String execAndGetResponseString(final Command command) throws IOException, InterruptedException {
        return execAndGetResponseString(command, null);
    }

    public static Process exec(final Command command) throws IOException {
        return exec(command, null);
    }

    synchronized public static String execAndGetResponseString(final Command command, final Map<String, String> env) throws IOException, InterruptedException {

        final Process process = exec(command, env);
        return getProcessReturn(process);
    }

    synchronized public static Process exec(final Command command, final Map<String, String> env) throws IOException {

        final ProcessBuilder builder = new ProcessBuilder(command.getLstCommands());
        final Map<String, String> currentEnv = builder.environment();
        if (env != null) {
            for (final Map.Entry<String, String> entry : env.entrySet()) {
                currentEnv.put(entry.getKey(), entry.getValue());
            }
        }
        return builder.start();
    }

    synchronized private static String getProcessReturn(final Process process) throws InterruptedException {

        final StreamGobblerThread errorGobbler = new RuntimeUtil().new StreamGobblerThread(process.getErrorStream());
        final StreamGobblerThread outputGobbler = new RuntimeUtil().new StreamGobblerThread(process.getInputStream());

        process.waitFor();
        errorGobbler.join();
        outputGobbler.join();

        final StringBuilder builder = new StringBuilder();
        builder.append(outputGobbler.getOutputstream());
        builder.append(errorGobbler.getOutputstream());
        final int length = builder.length();
        final int lastLineBreak = builder.lastIndexOf(LINE_BREAK);
        if (length > 0 && lastLineBreak == length - 1) {
            builder.deleteCharAt(lastLineBreak);
        }
        builder.trimToSize();
        return builder.toString();
    }

    // ---------------------------------------------------------------------------------------------
    // Inner Class.
    // ---------------------------------------------------------------------------------------------
    public static class Command {

        private final List<String> lstCommands = new ArrayList<>();

        // ---------------------------------------------------------------------------------------------
        // Constructors.
        // ---------------------------------------------------------------------------------------------
        public Command(final String... command) {
            for (final String arg : command) {
                final String[] split = arg.split(SPACE_STRING);
                for (final String args : split) {
                    this.lstCommands.add(args);
                }
            }
        }

        public List<String> getLstCommands() {
            return this.lstCommands;
        }

        // ---------------------------------------------------------------------------------------------
        // * @see java.lang.Object#toString()
        // ---------------------------------------------------------------------------------------------
        @Override
        public String toString() {
            return this.lstCommands.stream()
                    .collect(Collectors.joining());
        }
    }

    private class StreamGobblerThread extends Thread {

        private final InputStream stream;
        private String outputstream;

        // ---------------------------------------------------------------------------------------------
        // Constructors.
        // ---------------------------------------------------------------------------------------------
        public StreamGobblerThread(final InputStream stream) {
            super();
            this.stream = stream;
            super.start();
        }

        // ---------------------------------------------------------------------------------------------
        // * @see java.lang.Thread#run()
        // ---------------------------------------------------------------------------------------------
        @Override
        public void run() {
            try (
                    final InputStreamReader reader = new InputStreamReader(this.stream);
                    final BufferedReader buffer = new BufferedReader(reader);) {

                final StringBuilder builder = new StringBuilder();
                String line = null;
                while ((line = buffer.readLine()) != null) {
                    builder.append(line);
                    builder.append(LINE_BREAK);
                }
                this.outputstream = builder.toString();
            } catch (final IOException e) {
                e.printStackTrace();
            }
        }

        // ---------------------------------------------------------------------------------------------
        // Get/Set.
        // ---------------------------------------------------------------------------------------------
        public String getOutputstream() {
            return this.outputstream;
        }
    }
}
