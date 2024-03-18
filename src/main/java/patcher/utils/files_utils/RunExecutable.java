package patcher.utils.files_utils;

import java.io.IOException;
import java.lang.ProcessBuilder.Redirect;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RunExecutable {
    public static Process runExec(String command, String[] args, Path workingDirectory, boolean redirectOutput) throws IOException {
        List<String> parametrizedCommand = new ArrayList<>();
        parametrizedCommand.add(command);
        if (args != null) {
            parametrizedCommand.addAll(Arrays.asList(args));
        }
        String[] params = new String[parametrizedCommand.size()];
        params = parametrizedCommand.toArray(params);

        ProcessBuilder pb = new ProcessBuilder(params);
        if (workingDirectory != null)
            pb.directory(workingDirectory.toFile());
        if (redirectOutput) {
            pb.redirectOutput(Redirect.INHERIT);
            pb.redirectError(Redirect.INHERIT);
        }
        return pb.start();
    }
    public static Process runExec(String command, boolean redirectOutput) throws IOException {
        return runExec(command, null, null, redirectOutput);
    }
    public static Process runExec(String command, Path workingDirectory, boolean redirectOutput) throws IOException {
        return runExec(command, null, workingDirectory, redirectOutput);
    }
    public static Process runExec(String command, String[] args, boolean redirectOutput) throws IOException {
        return runExec(command, args, null, redirectOutput);
    }
}
