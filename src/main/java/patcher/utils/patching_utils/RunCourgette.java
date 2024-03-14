package patcher.utils.patching_utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;

import patcher.utils.files_utils.RunExecutable;
import patcher.utils.files_utils.Directories;

public class RunCourgette {
    public static String os = System.getProperty("os.name").toLowerCase();
    public static boolean allowConsoleOutput = false;

    String[] courgetteArgs = null;
    boolean replaceFiles;

    public static void unpackCourgette() throws IOException {
        Directories.deleteDirectory("tmp");

        if (os.contains("windows")) {
            Directories.unpackResources("/win", "tmp");
        } else if (os.contains("linux")) {
            Directories.unpackResources("/linux", "tmp");
        }
    }

    public Process runExec(String[] args, boolean redirectOutput) throws IOException, InterruptedException {
        Process courgette = null;
        if (os.contains("windows")) {
            courgette = RunExecutable.runExec("tmp/win/courgette.exe", args, redirectOutput);
        } else if (os.contains("linux")) {
            courgette = RunExecutable.runExec("tmp/linux/courgette", args, redirectOutput);
        }
        if (allowConsoleOutput) {
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(courgette.getInputStream()));
            BufferedReader stdError = new BufferedReader(new InputStreamReader(courgette.getErrorStream()));

            // Read the output from the command
            String s = null;
            while ((s = stdInput.readLine()) != null) {
                System.out.println(s);
            }

            // Read any errors from the attempted command
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
    
    public void run(String[] args, boolean _replaceFiles, boolean redirectOutput) throws IOException, InterruptedException {
        courgetteArgs = args;
        replaceFiles = _replaceFiles;
        runExec(courgetteArgs, redirectOutput);
    }
}
