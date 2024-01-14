package patching;

import java.util.LinkedList;
import java.util.List;

import org.bitbucket.cowwoc.diffmatchpatch.DiffMatchPatch;
import org.bitbucket.cowwoc.diffmatchpatch.DiffMatchPatch.Patch;
import org.json.JSONArray;
import org.json.JSONObject;

public class Patcher {
    public static Object[] applyPatch(String oldData, List<DiffMatchPatch.Patch> patch) {
        DiffMatchPatch patcher = new DiffMatchPatch();
        return patcher.patchApply((LinkedList<Patch>) patch, oldData);
    }
    public static Object[] applyPatch(byte[] oldData, List<DiffMatchPatch.Patch> patch) {
        return applyPatch(DataEncoder.encode(oldData), patch);
    }

    public static String applyCustomPatch(String _oldData, JSONArray patch) {
        String newData = new String(_oldData);

        for (int i = 0; i < patch.length(); ++i) {
            JSONObject item = patch.getJSONObject(i);

            if (item.getBoolean("mode")) { // if mode is INSERT
                newData = newData.substring(0, item.getInt("position")) +
                        item.getString("data") + newData.substring(item.getInt("position"));
            } else {
                newData = newData.substring(0, item.getInt("position")) +
                        newData.substring(item.getInt("position")).replaceFirst(item.getString("data"), "");
            }
        }

        return newData;
    }
    public static String applyCustomPatch(byte[] oldData, JSONArray patch) {
        return applyCustomPatch(DataEncoder.encode(oldData), patch);
    }
}
