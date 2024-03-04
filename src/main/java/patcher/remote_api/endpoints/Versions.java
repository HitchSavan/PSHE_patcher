package patcher.remote_api.endpoints;

import java.io.IOException;
import java.util.Map;

import org.json.JSONObject;

import patcher.remote_api.utils.Connector;

public class Versions {
    private static final String baseEndpoint = "/versions";
    private static final String historyEndpoint = baseEndpoint + "/history";
    private static final String rootEndpoint = baseEndpoint + "/root";
    private static final String switchEndpoint = baseEndpoint + "/switch";

    public static JSONObject getVersions(Map<String, String> params) throws IOException {
        return Connector.connect(baseEndpoint, Connector.Methods.GET, params);
    }
    public static JSONObject postVersions(JSONObject data, Map<String, String> params) throws IOException {
        return Connector.connect(baseEndpoint, Connector.Methods.POST, params, data);
    }
    public static JSONObject deleteVersions(Map<String, String> params) throws IOException {
        return Connector.connect(baseEndpoint, Connector.Methods.DELETE, params);
    }
    public static JSONObject getHistory(Map<String, String> params) throws IOException {
        return Connector.connect(historyEndpoint, Connector.Methods.GET, params);
    }
    public static JSONObject putRoot(JSONObject data, Map<String, String> params) throws IOException {
        return Connector.connect(rootEndpoint, Connector.Methods.PUT, params, data);
    }
    public static JSONObject getSwitch(Map<String, String> params) throws IOException {
        return Connector.connect(switchEndpoint, Connector.Methods.GET, params);
    }

    public static JSONObject getHistory() throws IOException {
        return getHistory(null);
    }
}
