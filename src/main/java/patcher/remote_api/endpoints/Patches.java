package patcher.remote_api.endpoints;

import java.io.IOException;
import java.util.Map;

import org.json.JSONObject;

import patcher.remote_api.utils.Connector;

public class Patches {
    private static final String baseEndpoint = "/patches";
    private static final String allPatchesEndpoint = baseEndpoint + "/all";
    private static final String fileEndpoint = baseEndpoint + "/file";
    private static final String infoEndpoint = fileEndpoint + "/info";

    public static int postPatches(JSONObject data, Map<String, String> params) throws IOException {
        Connector.connect(baseEndpoint, Connector.Methods.POST, params, data);
        return Connector.getResponseCode();
    }
    public static int deletePatches(Map<String, String> params) throws IOException {
        Connector.connect(baseEndpoint, Connector.Methods.DELETE, params);
        return Connector.getResponseCode();
    }
    public static JSONObject getAll() throws IOException {
        return Connector.connect(allPatchesEndpoint, Connector.Methods.GET);
    }
    public static JSONObject getFile(Map<String, String> params) throws IOException {
        return Connector.connect(fileEndpoint, Connector.Methods.GET, params);
    }
    public static int postFile(JSONObject data, Map<String, String> params) throws IOException {
        Connector.connect(fileEndpoint, Connector.Methods.POST, params, data);
        return Connector.getResponseCode();
    }
    public static JSONObject getInfo(Map<String, String> params) throws IOException {
        return Connector.connect(infoEndpoint, Connector.Methods.GET, params);
    }

    public static int postPatches(JSONObject data) throws IOException {
        return postPatches(data, null);
    }
    public static int deletePatches() throws IOException {
        return deletePatches();
    }
    public static JSONObject getFile() throws IOException {
        return getFile(null);
    }
    public static int postFile(JSONObject data) throws IOException {
        return postFile(data, null);
    }
    public static JSONObject getInfo() throws IOException {
        return getInfo(null);
    }
}
