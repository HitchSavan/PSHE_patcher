package patcher.utils.patching_utils;

public class Patcher {
    public static void generatePatch(String oldFile, String newFile, String patchFile, boolean redirectOutput) {
        RunCourgette courgetteInstance = new RunCourgette();
        String[] args = {"-gen", oldFile, newFile, patchFile};
        if (RunCourgette.allowConsoleOutput) {
            for (int k = 0; k < args.length; ++k) {
                System.out.print(args[k]);
                System.out.print("\t");
            }
            System.out.println();
        }
        courgetteInstance.run(args, false, redirectOutput);
    }

    public static void applyPatch(String oldFile, String newFile, String patchFile, boolean replaceFiles, boolean redirectOutput) {
        RunCourgette courgetteInstance = new RunCourgette();
        String[] args = {"-apply", oldFile, patchFile, newFile};
        if (RunCourgette.allowConsoleOutput) {
            for (int i = 0; i < args.length; ++i) {
                System.out.print(args[i]);
                System.out.print("\t");
            }
            System.out.println();
        }
        courgetteInstance.run(args, replaceFiles, redirectOutput);
    }
}
