package patcher.remote_api.endpoints;

import java.io.IOException;
import java.util.Map;

import org.json.JSONObject;

import patcher.remote_api.utils.Connector;
import patcher.remote_api.utils.Methods;

public class VersionsEndpoint {
    private static final String baseEndpoint = "/versions";
    private static final String historyEndpoint = baseEndpoint + "/history";
    private static final String rootEndpoint = baseEndpoint + "/root";
    private static final String switchEndpoint = baseEndpoint + "/switch";

    public static JSONObject getVersions(Map<String, String> params) throws IOException {
        return Connector.connect(baseEndpoint, Methods.GET, params);
    }
    public static JSONObject postVersions(byte[] data, Map<String, String> params) throws IOException {
        return Connector.connect(baseEndpoint, Methods.POST, params, data);
    }
    public static JSONObject deleteVersions(Map<String, String> params) throws IOException {
        return Connector.connect(baseEndpoint, Methods.DELETE, params);
    }
    public static JSONObject getHistory(Map<String, String> params) throws IOException {
        return Connector.connect(historyEndpoint, Methods.GET, params);
    }
    public static JSONObject putRoot(byte[] data, Map<String, String> params) throws IOException {
        return Connector.connect(rootEndpoint, Methods.PUT, params, data);
    }
    public static JSONObject getSwitch(Map<String, String> params) throws IOException {
        return Connector.connect(switchEndpoint, Methods.GET, params);
    }

    public static JSONObject getHistory() throws IOException {
        return getHistory(null);
    }
}
