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
    public static int postVersions(JSONObject data, Map<String, String> params) throws IOException {
        Connector.connect(baseEndpoint, Connector.Methods.POST, params, data);
        return Connector.getResponseCode();
    }
    public static int deleteVersions(Map<String, String> params) throws IOException {
        Connector.connect(baseEndpoint, Connector.Methods.DELETE, params);
        return Connector.getResponseCode();
    }
    public static JSONObject getHistory(Map<String, String> params) throws IOException {
        return Connector.connect(historyEndpoint, Connector.Methods.GET, params);
    }
    public static int putRoot(JSONObject data, Map<String, String> params) throws IOException {
        Connector.connect(rootEndpoint, Connector.Methods.PUT, params, data);
        return Connector.getResponseCode();
    }
    public static JSONObject getSwitch(Map<String, String> params) throws IOException {
        return Connector.connect(switchEndpoint, Connector.Methods.GET, params);
    }

    public static int postVersions(JSONObject data) throws IOException {
        return postVersions(data, null);
    }
    public static int deleteVersions() throws IOException {
        return deleteVersions(null);
    }
    public static JSONObject getHistory() throws IOException {
        return getHistory(null);
    }
    public static int putRoot(JSONObject data) throws IOException {
        return putRoot(data, null);
    }
    public static JSONObject getVersions() throws IOException {
        return getVersions(null);
    }
    public static JSONObject getSwitch() throws IOException {
        return getSwitch(null);
    }
}
