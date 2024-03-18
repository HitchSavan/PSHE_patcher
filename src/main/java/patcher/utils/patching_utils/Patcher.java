package patcher.utils.patching_utils;

import java.io.IOException;
import java.nio.file.Path;

public class Patcher {
    public static void generatePatch(Path oldAbsoluteFile, Path newAbsoluteFile, Path patchAbsoluteFile,
            Path projectPath, boolean redirectOutput) throws IOException, InterruptedException {
        Path projectParent = projectPath.getParent();
        String[] args = {"-gen",
                projectParent.relativize(oldAbsoluteFile).toString(),
                projectParent.relativize(newAbsoluteFile).toString(),
                projectParent.relativize(patchAbsoluteFile).toString()
            };
        execute(args, projectPath, false, redirectOutput);
    }

    public static void applyPatch(Path oldAbsoluteFile, Path newAbsoluteFile, Path patchAbsoluteFile,
            Path projectPath, boolean replaceFiles, boolean redirectOutput) throws IOException, InterruptedException {
        Path projectParent = projectPath.getParent();
        String[] args = {"-apply",
                projectParent.relativize(oldAbsoluteFile).toString(),
                projectParent.relativize(patchAbsoluteFile).toString(),
                projectParent.relativize(newAbsoluteFile).toString()
            };
        execute(args, projectPath, replaceFiles, redirectOutput);
    }

    private static void execute(String[] args, Path projectPath, boolean replaceFiles, boolean redirectOutput) throws IOException, InterruptedException {
        RunCourgette courgetteInstance = new RunCourgette();
        if (RunCourgette.allowConsoleOutput) {
            for (int i = 0; i < args.length; ++i) {
                System.out.print(args[i]);
                System.out.print("\t");
            }
            System.out.println();
        }
        courgetteInstance.run(args, projectPath.getParent(), replaceFiles, redirectOutput);
    }
}
