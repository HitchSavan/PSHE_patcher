package patching;

import java.util.LinkedList;
import java.util.List;

import org.bitbucket.cowwoc.diffmatchpatch.DiffMatchPatch;
import org.bitbucket.cowwoc.diffmatchpatch.DiffMatchPatch.Patch;

public class Patcher {
    public static Object[] applyPatch(String oldData, List<DiffMatchPatch.Patch> patch) {
        DiffMatchPatch patcher = new DiffMatchPatch();
        return patcher.patchApply((LinkedList<Patch>) patch, oldData);
    }

    public static Object[] applyPatch(byte[] oldData, List<DiffMatchPatch.Patch> patch) {
        return applyPatch(DataEncoder.encode(oldData), patch);
    }
}
