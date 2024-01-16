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

    public static String revertCustomPatch(String _newData, JSONArray patch) {
        String oldData = new String(_newData);

        for (int i = patch.length()-1; i >= 0; --i) {
            JSONObject item = patch.getJSONObject(i);

            if (item.getBoolean("mode")) { // if mode is INSERT
                oldData = oldData.substring(0, item.getInt("position")) +
                        oldData.substring(item.getInt("position")).replaceFirst(item.getString("data"), "");
            } else {
                oldData = oldData.substring(0, item.getInt("position")) +
                        item.getString("data") + oldData.substring(item.getInt("position"));
            }
        }

        return oldData;
    }
    public static String revertCustomPatch(byte[] oldData, JSONArray patch) {
        return revertCustomPatch(DataEncoder.encode(oldData), patch);
    }
}
