package patching;

import java.util.LinkedList;

import org.bitbucket.cowwoc.diffmatchpatch.DiffMatchPatch;

public class PatchCreator {

    public static LinkedList<DiffMatchPatch.Diff> getDiff(String oldData, String newData) {
        DiffMatchPatch dmp = new DiffMatchPatch();
        LinkedList<DiffMatchPatch.Diff> diff = dmp.diffMain(oldData, newData);
        return diff;
    }

    public static LinkedList<DiffMatchPatch.Diff> getDiff(byte[] oldData, byte[] newData) {
        return getDiff(DataEncoder.encode(oldData), DataEncoder.encode(newData));
    }
    
    public static LinkedList<DiffMatchPatch.Patch> getPatch(String oldData, String newData) {
        DiffMatchPatch patcher = new DiffMatchPatch();
        return patcher.patchMake(oldData, getDiff(oldData, newData));
    }

    public static LinkedList<DiffMatchPatch.Patch> getPatch(byte[] oldData, byte[] newData) {
        return getPatch(DataEncoder.encode(oldData), DataEncoder.encode(newData));
    }
}
