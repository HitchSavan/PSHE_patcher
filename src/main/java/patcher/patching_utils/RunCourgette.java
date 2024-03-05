package patcher.patching_utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;

import patcher.files_utils.RunExecutable;
import patcher.files_utils.UnpackResources;

public class RunCourgette {
    public static String os = System.getProperty("os.name").toLowerCase();
    public static boolean allowConsoleOutput = false;

    String[] courgetteArgs = null;
    boolean replaceFiles;

    public static void unpackCourgette() {
        UnpackResources.deleteDirectory("tmp");

        try {
            if (os.contains("windows")) {
                UnpackResources.unpackResources("/win", "tmp");
            } else if (os.contains("linux")) {
                UnpackResources.unpackResources("/linux", "tmp");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Process runExec(String[] args) throws IOException, InterruptedException {
        Process courgette = null;
        if (os.contains("windows")) {
            courgette = RunExecutable.runExec("tmp/win/courgette.exe", args);
        } else if (os.contains("linux")) {
            courgette = RunExecutable.runExec("tmp/linux/courgette", args);
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
    
    public void run(String[] args, boolean _replaceFiles) {
        courgetteArgs = args;
        replaceFiles = _replaceFiles;
        try {
            runExec(courgetteArgs);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
