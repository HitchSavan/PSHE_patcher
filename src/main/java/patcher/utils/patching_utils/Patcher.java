package patcher.utils.patching_utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

public class Patcher {
    public static void generatePatch(Path oldAbsoluteFile, Path newAbsoluteFile, Path patchAbsoluteFile,
            Path courgetteWorkingDirectory, boolean redirectOutput) throws IOException, InterruptedException {

        Files.createDirectories(newAbsoluteFile.getParent());

        Path oldMovedFile = courgetteWorkingDirectory.resolve(Paths.get("courgette_old", oldAbsoluteFile.getFileName().toString()));
        Path newMovedFile = courgetteWorkingDirectory.resolve(Paths.get("courgette_new", newAbsoluteFile.getFileName().toString()));
        Path patchMovedFile = courgetteWorkingDirectory.resolve(Paths.get("courgette_patch", patchAbsoluteFile.getFileName().toString()));

        Files.copy(oldAbsoluteFile, oldMovedFile, StandardCopyOption.REPLACE_EXISTING);
        Files.copy(newAbsoluteFile, newMovedFile, StandardCopyOption.REPLACE_EXISTING);

        String[] args = {"-gen",
                oldMovedFile.toString(),
                newMovedFile.toString(),
                patchMovedFile.toString()
            };
        execute(args, courgetteWorkingDirectory, false, redirectOutput);

        Files.move(patchMovedFile, patchAbsoluteFile, StandardCopyOption.REPLACE_EXISTING);
        Files.delete(oldMovedFile);
        Files.delete(newMovedFile);
    }

    public static void applyPatch(Path oldAbsoluteFile, Path newAbsoluteFile, Path patchAbsoluteFile,
            Path courgetteWorkingDirectory, boolean replaceFiles, boolean redirectOutput) throws IOException, InterruptedException {

        Files.createDirectories(newAbsoluteFile.getParent());

        Path oldMovedFile = courgetteWorkingDirectory.resolve(Paths.get("courgette_files", "old", oldAbsoluteFile.getFileName().toString()));
        Path newMovedFile = courgetteWorkingDirectory.resolve(Paths.get("courgette_files", "new", newAbsoluteFile.getFileName().toString()));
        Path patchMovedFile = courgetteWorkingDirectory.resolve(Paths.get("courgette_files", "patch", patchAbsoluteFile.getFileName().toString()));

        List.of(oldMovedFile, newMovedFile, patchMovedFile).forEach(file -> {
            file.getParent().toFile().mkdirs();
        });

        Files.copy(oldAbsoluteFile, oldMovedFile, StandardCopyOption.REPLACE_EXISTING);
        Files.copy(patchAbsoluteFile, patchMovedFile, StandardCopyOption.REPLACE_EXISTING);

        String[] args = {"-apply",
                oldMovedFile.toString(),
                patchMovedFile.toString(),
                newMovedFile.toString()
            };
        execute(args, courgetteWorkingDirectory, replaceFiles, redirectOutput);

        Files.move(newMovedFile, newAbsoluteFile, StandardCopyOption.REPLACE_EXISTING);
        Files.delete(oldMovedFile);
        Files.delete(patchMovedFile);
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
}
