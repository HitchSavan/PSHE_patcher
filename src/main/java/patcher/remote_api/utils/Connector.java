package patcher.remote_api.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import lombok.Getter;
import lombok.Setter;
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

    @Getter @Setter
    private static String baseUrl = "http://tarkov.deadlauncher.fun/api/v1";

    @Getter @Setter
    private static int responseCode;

    public static JSONObject connect(String endpointUrl, Methods method, Map<String, String> parameters) throws IOException {
        endpointUrl = getBaseUrl() + endpointUrl;
        URL url = parameters != null && !parameters.isEmpty() ?
                ParamsBuilder.getUrlWithParams(endpointUrl, parameters) : new URL(endpointUrl);

        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod(methodStrings.get(method));
        int responseCode = con.getResponseCode();
        setResponseCode(responseCode);

        JSONObject response = new JSONObject();

        switch (method) {
            case GET:
                StringBuffer content = new StringBuffer();
                if (responseCode > 299) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        content.append(inputLine);
                    }
                    in.close();
                } else {
                    content.append(new JSONObject().put("error", responseCode));
                }
                response = new JSONObject(content.toString());
                break;
        
            case PUT:
            
                break;
        
            case POST:
            
                break;
        
            case DELETE:
            
                break;
        
            default:
                break;
        }

        con.disconnect();

        return response;
    }

    public static JSONObject connect(String endpointUrl, Methods method) throws IOException {
        return connect(endpointUrl, method, null);
    }
}
