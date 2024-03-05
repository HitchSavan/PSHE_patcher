package patcher.remote_api.endpoints;

import java.io.IOException;

import org.json.JSONObject;

import patcher.remote_api.utils.Connector;
import patcher.remote_api.utils.Methods;

public class Service {
    private static final String endpoint = "/service/ping";

    public static JSONObject ping() throws IOException {
        return Connector.connect(endpoint, Methods.GET);
    }
}
