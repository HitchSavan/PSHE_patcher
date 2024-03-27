package patcher.utils.patching_utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

import patcher.utils.files_utils.Directories;

public class Patcher {
    public static void generatePatch(Path oldAbsoluteFile, Path newAbsoluteFile, Path patchAbsoluteFile,
            Path courgetteWorkingDirectory, boolean redirectOutput) throws IOException, InterruptedException {

        initFiles(oldAbsoluteFile, newAbsoluteFile);
        boolean workInOldPlace = courgetteWorkingDirectory.equals(oldAbsoluteFile.getParent());
        boolean workInNewPlace = courgetteWorkingDirectory.equals(newAbsoluteFile.getParent());

        Path oldRelativeFile;
        Path newRelativeFile;
        Path patchRelativeFile;
        if (workInOldPlace) {
            oldRelativeFile = oldAbsoluteFile;
        } else {
            oldRelativeFile = courgetteWorkingDirectory.resolve(Paths.get("courgette_files", "old", oldAbsoluteFile.getFileName().toString()));
        }
        if (workInNewPlace) {
            newRelativeFile = newAbsoluteFile;
        } else {
            newRelativeFile = courgetteWorkingDirectory.resolve(Paths.get("courgette_files", "new", newAbsoluteFile.getFileName().toString()));
        }
        patchRelativeFile = courgetteWorkingDirectory.resolve(Paths.get("courgette_files", "patch", patchAbsoluteFile.getFileName().toString()));

        List.of(oldRelativeFile, newRelativeFile, patchRelativeFile).forEach(file -> {
            file.getParent().toFile().mkdirs();
        });

        Files.copy(oldAbsoluteFile, oldRelativeFile, StandardCopyOption.REPLACE_EXISTING);
        Files.copy(newAbsoluteFile, newRelativeFile, StandardCopyOption.REPLACE_EXISTING);

        String[] args = {"-gen",
                courgetteWorkingDirectory.relativize(oldRelativeFile).toString(),
                courgetteWorkingDirectory.relativize(newRelativeFile).toString(),
                courgetteWorkingDirectory.relativize(patchRelativeFile).toString()
            };
        execute(args, courgetteWorkingDirectory, false, redirectOutput);

        Files.move(patchRelativeFile, patchAbsoluteFile, StandardCopyOption.REPLACE_EXISTING);

        if (workInOldPlace || workInNewPlace) {
            Directories.deleteDirectory(courgetteWorkingDirectory.resolve("courgette_files"));
        } else {
            Files.delete(oldRelativeFile);
            Files.delete(newRelativeFile);
        }
    }

    public static void applyPatch(Path oldAbsoluteFile, Path newAbsoluteFile, Path patchAbsoluteFile,
            Path courgetteWorkingDirectory, boolean replaceFiles, boolean redirectOutput) throws IOException, InterruptedException {

        initFiles(oldAbsoluteFile, newAbsoluteFile);
        boolean workInplace = courgetteWorkingDirectory.equals(oldAbsoluteFile.getParent());

        Path oldRelativeFile;
        Path newRelativeFile;
        Path patchRelativeFile;
        if (workInplace) {
            oldRelativeFile = oldAbsoluteFile;
        } else {
            oldRelativeFile = courgetteWorkingDirectory.resolve(Paths.get("courgette_files", "old", oldAbsoluteFile.getFileName().toString()));
        }
        newRelativeFile = courgetteWorkingDirectory.resolve(Paths.get("courgette_files", "new", newAbsoluteFile.getFileName().toString()));
        patchRelativeFile = courgetteWorkingDirectory.resolve(Paths.get("courgette_files", "patch", patchAbsoluteFile.getFileName().toString()));

        List.of(oldRelativeFile, newRelativeFile, patchRelativeFile).forEach(file -> {
            try {
                Files.createDirectories(file.getParent());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        Files.copy(oldAbsoluteFile, oldRelativeFile, StandardCopyOption.REPLACE_EXISTING);
        Files.copy(patchAbsoluteFile, patchRelativeFile, StandardCopyOption.REPLACE_EXISTING);

        String[] args = {"-apply",
                courgetteWorkingDirectory.relativize(oldRelativeFile).toString(),
                courgetteWorkingDirectory.relativize(patchRelativeFile).toString(),
                courgetteWorkingDirectory.relativize(newRelativeFile).toString()
            };
        execute(args, courgetteWorkingDirectory, replaceFiles, redirectOutput);

        Files.move(newRelativeFile, newAbsoluteFile, StandardCopyOption.REPLACE_EXISTING);
        Files.delete(oldRelativeFile);
        Files.delete(patchRelativeFile);
    }

    private static void execute(String[] args, Path courgetteWorkingDirectory, boolean replaceFiles, boolean redirectOutput) throws IOException, InterruptedException {
        RunCourgette courgetteInstance = new RunCourgette();
        if (RunCourgette.allowConsoleOutput) {
            for (int i = 0; i < args.length; ++i) {
                System.out.print(args[i]);
                System.out.print("\t");
            }
            System.out.println();
        }
        courgetteInstance.run(args, courgetteWorkingDirectory, replaceFiles, redirectOutput);
    }

    private static void initFiles(Path oldAbsoluteFile, Path newAbsoluteFile) throws IOException {
        Files.createDirectories(newAbsoluteFile.getParent());
        if (!Files.exists(oldAbsoluteFile)) {
            Files.createDirectories(oldAbsoluteFile.getParent());
            Files.createFile(oldAbsoluteFile);
            byte[] emptyData = {0};
            Files.write(oldAbsoluteFile, emptyData);
        }
    }
}
