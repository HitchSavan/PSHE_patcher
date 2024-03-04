package patcher.remote_api.endpoints;

import java.io.IOException;
import java.util.Map;

import org.json.JSONObject;

import patcher.remote_api.utils.Connector;

public class Files {
    private static final String baseEndpoint = "/files";
    private static final String allFilesEndpoint = baseEndpoint + "/all";
    private static final String historyEndpoint = baseEndpoint + "/history";
    private static final String versionEndpoint = baseEndpoint + "/version";
    private static final String rootEndpoint = baseEndpoint + "/root";

    public static JSONObject getFiles(Map<String, String> params) throws IOException {
        return Connector.connect(baseEndpoint, Connector.Methods.GET, params);
    }
    public static int deleteFiles(Map<String, String> params) throws IOException {
        Connector.connect(baseEndpoint, Connector.Methods.DELETE, params);
        return Connector.getResponseCode();
    }
    public static JSONObject getAll() throws IOException {
        return Connector.connect(allFilesEndpoint, Connector.Methods.GET);
    }
    public static JSONObject getHistory(Map<String, String> params) throws IOException {
        return Connector.connect(historyEndpoint, Connector.Methods.GET, params);
    }
    public static JSONObject getRoot(Map<String, String> params) throws IOException {
        return Connector.connect(rootEndpoint, Connector.Methods.GET, params);
    }
    public static int postRoot(JSONObject data, Map<String, String> params) throws IOException {
        Connector.connect(rootEndpoint, Connector.Methods.POST, params, data);
        return Connector.getResponseCode();
    }
    public static JSONObject getVersion(Map<String, String> params) throws IOException {
        return Connector.connect(versionEndpoint, Connector.Methods.GET, params);
    }
    public static int postVersion(JSONObject data, Map<String, String> params) throws IOException {
        Connector.connect(versionEndpoint, Connector.Methods.POST, params, data);
        return Connector.getResponseCode();
    }
    public static int deleteVersion(Map<String, String> params) throws IOException {
        Connector.connect(versionEndpoint, Connector.Methods.DELETE, params);
        return Connector.getResponseCode();
    }

    public static JSONObject getFiles() throws IOException {
        return getFiles(null);
    }
    public static int deleteFiles() throws IOException {
        return deleteFiles();
    }
    public static JSONObject getHistory() throws IOException {
        return getHistory(null);
    }
    public static JSONObject getRoot() throws IOException {
        return getRoot(null);
    }
    public static int postRoot(JSONObject data) throws IOException {
        return postRoot(data, null);
    }
    public static JSONObject getVersion() throws IOException {
        return getVersion(null);
    }
    public static int postVersion(JSONObject data) throws IOException {
        return postVersion(data);
    }
    public static int deleteVersion() throws IOException {
        return deleteVersion(null);
    }
}
