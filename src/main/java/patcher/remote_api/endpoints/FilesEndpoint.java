package patcher.remote_api.endpoints;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

import org.json.JSONObject;

import patcher.utils.remote_utils.Connector;
import patcher.utils.remote_utils.Methods;

public class FilesEndpoint {
    private static final String baseEndpoint = "/files";
    private static final String allFilesEndpoint = baseEndpoint + "/all";
    private static final String historyEndpoint = baseEndpoint + "/history";
    private static final String versionEndpoint = baseEndpoint + "/version";
    private static final String rootEndpoint = baseEndpoint + "/root";

    public static JSONObject getFiles(Map<String, String> params) throws IOException {
        return Connector.connect(baseEndpoint, Methods.GET, params);
    }
    public static JSONObject deleteFiles(Map<String, String> params) throws IOException {
        return Connector.connect(baseEndpoint, Methods.DELETE, params);
    }
    public static JSONObject getAll() throws IOException {
        return Connector.connect(allFilesEndpoint, Methods.GET);
    }
    public static JSONObject getHistory(Map<String, String> params) throws IOException {
        return Connector.connect(historyEndpoint, Methods.GET, params);
    }
    public static JSONObject getRoot(Path filepath, Map<String, String> params) throws IOException {
        return Connector.downloadFile(rootEndpoint, filepath, params);
    }
    public static JSONObject postRoot(byte[] data, Map<String, String> params) throws IOException {
        return Connector.connect(rootEndpoint, Methods.POST, params, data);
    }
    public static JSONObject getVersion(Map<String, String> params) throws IOException {
        return Connector.connect(versionEndpoint, Methods.GET, params);
    }
    public static JSONObject postVersion(byte[] data, Map<String, String> params) throws IOException {
        return Connector.connect(versionEndpoint, Methods.POST, params, data);
    }
    public static JSONObject deleteVersion(Map<String, String> params) throws IOException {
        return Connector.connect(versionEndpoint, Methods.DELETE, params);
    }
}
