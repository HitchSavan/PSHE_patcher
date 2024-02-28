package patcher.patching_utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;

import javafx.application.Platform;
import javafx.scene.control.Label;
import patcher.files_utils.RunExecutable;
import patcher.files_utils.UnpackResources;

public class RunCourgette extends Thread {

    public static int MAX_THREADS_AMOUNT = 10;
    public static String os = System.getProperty("os.name").toLowerCase();
    private static int currentThreadsAmount = 0;
    // private static Semaphore courgetteSemaphore = new Semaphore(1);

    String[] courgetteArgs = null;
    boolean replaceFiles;
    Label updatingComponent;

    // public static Semaphore courgSemaphore() {
    //     return courgetteSemaphore;
    // }

    public synchronized static int currentThreadsAmount() {
        return currentThreadsAmount;
    }

    public synchronized static void increaseThreadsAmount() {
        ++currentThreadsAmount;
    }

    public synchronized static void decreaseThreadsAmount() {
        --currentThreadsAmount;
    }

    public static void updateComponent(Label updatingComponent) {
        if (updatingComponent != null) {
            // try {
            //     courgSemaphore().acquire();
                Platform.runLater(() -> {
                    updatingComponent.setText("Active Courgette instances:\t" + RunCourgette.currentThreadsAmount());
                });
            // } catch (InterruptedException e) {
            //     e.printStackTrace();
            // } finally {
            //     courgSemaphore().release();
            // };
        }
    }

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
        System.out.println("OS name\t -> " + System.getProperty("os.name"));
        System.out.println("OS version\t -> " + System.getProperty("os.version"));
        System.out.println("OS Architecture\t -> " + System.getProperty("os.arch"));
        System.out.println();

        Process courgette = null;
        if (os.contains("windows")) {
            courgette = RunExecutable.runExec("tmp/win/courgette.exe", args);
        } else if (os.contains("linux")) {
            courgette = RunExecutable.runExec("tmp/linux/courgette", args);
        }

        while (RunCourgette.currentThreadsAmount() >= MAX_THREADS_AMOUNT) {
            sleep(1000);
        }

        RunCourgette.increaseThreadsAmount();
        updateComponent(updatingComponent);

        
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
        
        courgette.waitFor();

        if (replaceFiles) {
            Files.delete(Paths.get(args[1]));
            Files.move(Paths.get(args[3]), Paths.get(args[1]));
            Files.delete(Paths.get(args[3]).getParent());
        }
        RunCourgette.decreaseThreadsAmount();
        updateComponent(updatingComponent);
        return courgette;
    }
    
    public void run(String[] args, boolean _replaceFiles, Label _updatingComponent) {
        courgetteArgs = args;
        replaceFiles = _replaceFiles;
        updatingComponent = _updatingComponent;
        start();
    }

    @Override
    public void run() {
        try {
            runExec(courgetteArgs);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
