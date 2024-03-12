package patcher.remote_api.utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import lombok.Getter;
import lombok.Setter;
import patcher.data_utils.DataEncoder;
import patcher.remote_api.ParamsBuilder;

public class Connector {
    private static Map<Methods, String> methodStrings = new HashMap<>(
        Map.of( Methods.GET, "GET",
                Methods.PUT, "PUT",
                Methods.POST, "POST",
                Methods.DELETE, "DELETE")
    );

    private static final String apiUrl = "/api/v1";
    @Getter
    private static String baseUrl = "http://127.0.0.1:5000" + apiUrl;
    @Getter @Setter
    private static int responseCode;

    public static void setBaseUrl(String newBaseUrl) {
        baseUrl = newBaseUrl + apiUrl;
    }

    private static HttpURLConnection initConnect(String endpointUrl, Methods method, Map<String, String> parameters) throws IOException {
        endpointUrl = getBaseUrl() + endpointUrl;
        System.out.println("Connecting to " + endpointUrl);
        URL url = parameters != null && !parameters.isEmpty() ?
                ParamsBuilder.getUrlWithParams(endpointUrl, parameters) : new URL(endpointUrl);

        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod(methodStrings.get(method));
        int responseCode = con.getResponseCode();
        setResponseCode(responseCode);

        return con;
    }

    public static JSONObject connect(String endpointUrl, Methods method, Map<String, String> parameters, byte[] data) throws IOException {
        HttpURLConnection con = initConnect(endpointUrl, method, parameters);

        JSONObject response = new JSONObject();

        if (responseCode < 300) {
            switch (method) {
                case GET:
                    response = get(con);
                    break;

                case PUT:
                    put(con, new JSONObject(DataEncoder.toString(data)));
                    break;

                case POST:
                    response = post(con, data);
                    break;

                default:
                    break;
            }
        }

        response.put("status", responseCode).put("message", con.getResponseMessage());

        con.disconnect();

        return response;
    }
    public static JSONObject connect(String endpointUrl, Methods method) throws IOException {
        return connect(endpointUrl, method, null, null);
    }
    public static JSONObject connect(String endpointUrl, Methods method, Map<String, String> parameters) throws IOException {
        return connect(endpointUrl, method, parameters, null);
    }
    public static JSONObject connect(String endpointUrl, Methods method, byte[] data) throws IOException {
        return connect(endpointUrl, method, null, data);
    }

    public static JSONObject downloadFile(String endpointUrl, Path filePath, Map<String, String> parameters) throws IOException {
        HttpURLConnection con = initConnect(endpointUrl, Methods.GET, parameters);

        JSONObject response = new JSONObject();

        InputStream input = con.getInputStream();
        byte[] buffer = new byte[4096];
        int n;

        File file = filePath.toFile();
        filePath.getParent().toFile().mkdirs();

        FileOutputStream output = new FileOutputStream(file);
        while ((n = input.read(buffer)) != -1) 
        {
            output.write(buffer, 0, n);
        }
        output.close();

        return response.put("status", responseCode).put("message", con.getResponseMessage());
    }

    private static JSONObject get(HttpURLConnection con) throws IOException {
        StringBuffer content = new StringBuffer();
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));

        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();

        return new JSONObject(content.toString());
    }

    private static void put(HttpURLConnection con, JSONObject data) {
        DataOutputStream dataOutputStream = null;
        try {
            con.setDoOutput(true);
            dataOutputStream = new DataOutputStream(con.getOutputStream());
            dataOutputStream.write(data.toString().getBytes());
        } catch (IOException exception) {
            exception.printStackTrace();
        }  finally {
            if (dataOutputStream != null) {
                try {
                    dataOutputStream.flush();
                    dataOutputStream.close();
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
            }
        }
    }

    private static JSONObject post(HttpURLConnection con, byte[] data) throws IOException {
        con.setDoInput(true);
        con.setDoOutput(true);

        try(OutputStream os = con.getOutputStream()) {
            os.write(data, 0, data.length);			
        }

        try(BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"))) {
            StringBuilder response = new StringBuilder();
            String responseLine = null;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            return new JSONObject(response.toString());
        }
    }
}
