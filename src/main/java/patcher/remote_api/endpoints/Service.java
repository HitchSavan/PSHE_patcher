package patcher.remote_api.endpoints;

import java.io.IOException;

import org.json.JSONObject;

import patcher.remote_api.utils.Connector;

public class Service {

    private static final String endpoint = "/service/ping";

    public static JSONObject ping() throws IOException {
        return Connector.connect(endpoint, Connector.Methods.GET);
    }
}
