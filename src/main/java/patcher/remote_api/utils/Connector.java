package patcher.remote_api.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import patcher.remote_api.ParamsBuilder;

public class Connector {

    public static enum Methods {
        GET,
        PUT,
        POST,
        DELETE
    }

    private static Map<Methods, String> methodStrings = new HashMap<>(
        Map.of( Methods.GET, "GET",
                Methods.PUT, "PUT",
                Methods.POST, "POST",
                Methods.DELETE, "DELETE")
    );

    private static String baseUrl = "http://tarkov.deadlauncher.fun/api/v1";

    public static String getBaseUrl() {
        return baseUrl;
    }

    public static void setBaseUrl(String baseUrl) {
        Connector.baseUrl = baseUrl;
    }

    public static JSONObject connect(String urlStr, Methods method, Map<String, String> parameters) throws IOException {
        URL url = ParamsBuilder.getUrlWithParams(urlStr, parameters);

        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod(methodStrings.get(method));

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer content = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();

        JSONObject response = new JSONObject(content.toString());

        return response;
    }
}
