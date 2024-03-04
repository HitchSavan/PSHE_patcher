package patcher.remote_api.endpoints;

import java.io.IOException;

import patcher.remote_api.utils.Connector;

public class Service {

    private static final String endpoint = "/service/ping";

    public static int ping() throws IOException {
        Connector.connect(endpoint, Connector.Methods.GET);
        return Connector.getResponseCode();
    }
}
