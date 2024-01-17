package patching;

import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;
import java.util.regex.PatternSyntaxException;

import org.bitbucket.cowwoc.diffmatchpatch.DiffMatchPatch;
import org.bitbucket.cowwoc.diffmatchpatch.DiffMatchPatch.Diff;
import org.bitbucket.cowwoc.diffmatchpatch.DiffMatchPatch.Operation;
import org.json.JSONArray;
import org.json.JSONObject;

public class PatchCreator {

    public static boolean compareFiles(String oldData, String newData) throws NoSuchAlgorithmException {
        return DataEncoder.encodeChecksum(oldData).equals(DataEncoder.encodeChecksum(newData));
    }

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
            if (item.operation == Operation.EQUAL) {
                i += item.text.length();
                continue;
            }

            JSONObject oneChangePatch = new JSONObject();
            oneChangePatch.put("position", i); // start position

            switch (item.operation) {
                case DELETE:
                    try {
                        oldData = oldData.substring(0, i) + oldData.substring(i).replaceFirst(item.text, "");
                    } catch (PatternSyntaxException e) {
                        item.text = "\\"+item.text;
                        oldData = oldData.substring(0, i) + oldData.substring(i).replaceFirst(item.text, "");
                    }
                    oneChangePatch.put("mode", false); // mode is INSERT (true) or DELETE (false)
                    break;

                case INSERT:
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

    public static JSONObject compressJson(JSONObject _obj) {
        JSONObject compressedJson = new JSONObject().put("f", _obj.getString("filename"))
                .put("p", new JSONArray());
        JSONArray patchArray = _obj.getJSONArray("patch");
        JSONObject patchItem;
        for (int i = 0; i < patchArray.length(); ++i) {
            patchItem = patchArray.getJSONObject(i);
            JSONArray littlePatchItem = new JSONArray()
                    .put(patchItem.getLong("id"))
                    .put(patchItem.getLong("position"))
                    .put(patchItem.getBoolean("mode"))
                    .put(patchItem.getString("data"));
            compressedJson.getJSONArray("p").put(littlePatchItem);
        }
        return compressedJson;
    }
    public static JSONObject decompressJson(JSONObject _obj) {
        JSONObject decompressedJson = new JSONObject().put("filename", _obj.getString("f"))
                .put("patch", new JSONArray());
        JSONArray patchArray = _obj.getJSONArray("p");
        JSONArray patchItem;
        for (int i = 0; i < patchArray.length(); ++i) {
            patchItem = patchArray.getJSONArray(i);
            JSONObject littlePatchItem = new JSONObject()
                    .put("id", patchItem.getLong(0))
                    .put("position", patchItem.getLong(1))
                    .put("mode", patchItem.getBoolean(2))
                    .put("data", patchItem.getString(3));
            decompressedJson.getJSONArray("patch").put(littlePatchItem);
        }
        return decompressedJson;
    }
}
