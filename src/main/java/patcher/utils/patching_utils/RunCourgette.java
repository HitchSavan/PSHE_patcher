package patcher.utils.patching_utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import patcher.utils.files_utils.RunExecutable;
import patcher.utils.files_utils.Directories;

public class RunCourgette {
    public static String os = System.getProperty("os.name").toLowerCase();
    public static boolean allowConsoleOutput = false;

    String[] courgetteArgs = null;
    boolean replaceFiles;
    static private Path courgettePath;

    public static Path unpackCourgette() throws IOException {
        Directories.deleteDirectory("tmp");
        String osName = null;
        String courgetteName = "courgette";

        if (os.contains("windows")) {
            osName = "win";
            courgetteName = courgetteName + ".exe";
        } else if (os.contains("linux")) {
            osName = "linux";
        } else {
            System.out.println("Cant detect user OS");
            System.exit(58008);
        }
        Directories.unpackResources(osName, Paths.get("tmp"));
        courgettePath = Paths.get("tmp", osName, courgetteName).toAbsolutePath();

        return courgettePath;
    }

    public Process runExec(String[] args, Path projectParentPath, boolean redirectOutput) throws IOException, InterruptedException {
        Process courgette = null;
        courgette = RunExecutable.runExec(courgettePath.toString(), args, projectParentPath, redirectOutput);

        if (allowConsoleOutput) {
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(courgette.getInputStream()));
            BufferedReader stdError = new BufferedReader(new InputStreamReader(courgette.getErrorStream()));

            String s = null;
            while ((s = stdInput.readLine()) != null) {
                System.out.println(s);
            }
            while ((s = stdError.readLine()) != null) {
                System.out.println(s);
            }
        }
        
        courgette.waitFor();

        if (replaceFiles) {
            Files.delete(Paths.get(args[1]));
            Files.move(Paths.get(args[3]), Paths.get(args[1]));
            Files.delete(Paths.get(args[3]).getParent());
        }

        return courgette;
    }
    
    public void run(String[] args, Path projectParentPath, boolean _replaceFiles, boolean redirectOutput) throws IOException, InterruptedException {
        courgetteArgs = args;
        replaceFiles = _replaceFiles;
        runExec(courgetteArgs, projectParentPath, redirectOutput);
    }
}
