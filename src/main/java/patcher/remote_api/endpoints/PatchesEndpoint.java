package patcher.remote_api.endpoints;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

import org.json.JSONObject;

import patcher.utils.remote_utils.Connector;
import patcher.utils.remote_utils.Methods;

public class PatchesEndpoint {
    private static final String baseEndpoint = "/patches";
    private static final String allPatchesEndpoint = baseEndpoint + "/all";
    private static final String fileEndpoint = baseEndpoint + "/file";
    private static final String infoEndpoint = fileEndpoint + "/info";

    public static JSONObject postPatches(byte[] data, Map<String, String> params) throws IOException {
        return Connector.connect(baseEndpoint, Methods.POST, params, data);
    }
    public static JSONObject deletePatches(Map<String, String> params) throws IOException {
        return Connector.connect(baseEndpoint, Methods.DELETE, params);
    }
    public static JSONObject getAll() throws IOException {
        return Connector.connect(allPatchesEndpoint, Methods.GET);
    }
    public static JSONObject getFile(Path filepath, Map<String, String> params) throws IOException {
        return Connector.downloadFile(fileEndpoint, filepath, params);
    }
    public static JSONObject postFile(byte[] data, Map<String, String> params) throws IOException {
        return Connector.connect(fileEndpoint, Methods.POST, params, data);
    }
    public static JSONObject deleteFile(Map<String, String> params) throws IOException {
        return Connector.connect(fileEndpoint, Methods.DELETE, params);
    }
    public static JSONObject getInfo(Map<String, String> params) throws IOException {
        return Connector.connect(infoEndpoint, Methods.GET, params);
    }
}
