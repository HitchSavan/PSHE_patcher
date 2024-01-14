package patching;

import java.util.LinkedList;
import java.util.regex.PatternSyntaxException;

import org.bitbucket.cowwoc.diffmatchpatch.DiffMatchPatch;
import org.bitbucket.cowwoc.diffmatchpatch.DiffMatchPatch.Diff;
import org.json.JSONArray;
import org.json.JSONObject;

import utils.Constants;

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

    public static JSONArray getCustomPatch(String _oldData, String _newData) {
        String oldData = new String(_oldData);
        LinkedList<DiffMatchPatch.Diff> updateResult = getDiff(oldData, _newData);
        JSONArray filePatch = new JSONArray();

        int i = 0;
        int curMod = 0;
        Diff item;
        // try to reduce patch size
        for (int curDiff = 0; curDiff < updateResult.size(); curDiff++) {

            item = updateResult.get(curDiff);
            if (item.operation.ordinal() == Constants.EQUAL.ordinal()) {
                i += item.text.length();
                continue;
            }

            JSONObject oneChangePatch = new JSONObject();
            oneChangePatch.put("position", i); // start position

            switch (item.operation.ordinal()) {
                case 0: // DELETE
                    try {
                        oldData = oldData.substring(0, i) + oldData.substring(i).replaceFirst(item.text, "");
                    } catch (PatternSyntaxException e) {
                        item.text = "\\"+item.text;
                        oldData = oldData.substring(0, i) + oldData.substring(i).replaceFirst(item.text, "");
                    }
                    oneChangePatch.put("mode", false); // mode is INSERT (true) or DELETE (false)
                    break;

                case 1: // INSERT
                    oldData = oldData.substring(0, i) + item.text + oldData.substring(i);
                    i += item.text.length();
                    oneChangePatch.put("mode", true); // mode is INSERT (true) or DELETE (false)
                    break;
            
                default:
                    break;
            }
            
            oneChangePatch.put("id", curMod); // id of modification
            oneChangePatch.put("data", item.text); // changed substring
            filePatch.put(oneChangePatch); // id of modification
            curMod += 1;
        }

        return filePatch;
    }
    public static JSONArray getCustomPatch(byte[] oldData, byte[] newData) {
        return getCustomPatch(DataEncoder.encode(oldData), DataEncoder.encode(newData));
    }
}
