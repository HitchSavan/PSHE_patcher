package patching;

import java.util.LinkedList;

import org.bitbucket.cowwoc.diffmatchpatch.DiffMatchPatch;

public class Patcher {
    public static Object[] applyPatch(String oldData, LinkedList<DiffMatchPatch.Patch> patch) {
        DiffMatchPatch patcher = new DiffMatchPatch();
        return patcher.patchApply(patch, oldData);
    }

    public static Object[] applyPatch(byte[] oldData, LinkedList<DiffMatchPatch.Patch> patch) {
        return applyPatch(DataEncoder.encode(oldData), patch);
    }
}
