package patcher.remote_api.endpoints;

import java.io.IOException;
import java.util.Map;

import org.json.JSONObject;

import patcher.remote_api.utils.Connector;
import patcher.remote_api.utils.Methods;

public class Patches {
    private static final String baseEndpoint = "/patches";
    private static final String allPatchesEndpoint = baseEndpoint + "/all";
    private static final String fileEndpoint = baseEndpoint + "/file";
    private static final String infoEndpoint = fileEndpoint + "/info";

    public static JSONObject postPatches(JSONObject data, Map<String, String> params) throws IOException {
        return Connector.connect(baseEndpoint, Methods.POST, params, data);
    }
    public static JSONObject deletePatches(Map<String, String> params) throws IOException {
        return Connector.connect(baseEndpoint, Methods.DELETE, params);
    }
    public static JSONObject getAll() throws IOException {
        return Connector.connect(allPatchesEndpoint, Methods.GET);
    }
    public static JSONObject getFile(Map<String, String> params) throws IOException {
        return Connector.connect(fileEndpoint, Methods.GET, params);
    }
    public static JSONObject postFile(JSONObject data, Map<String, String> params) throws IOException {
        return Connector.connect(fileEndpoint, Methods.POST, params, data);
    }
    public static JSONObject deleteFile(Map<String, String> params) throws IOException {
        return Connector.connect(fileEndpoint, Methods.DELETE, params);
    }
    public static JSONObject getInfo(Map<String, String> params) throws IOException {
        return Connector.connect(infoEndpoint, Methods.GET, params);
    }
}
