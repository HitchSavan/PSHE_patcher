package patcher.remote_api;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

public class ParamsBuilder {
    public static URL getUrlWithParams(String baseUrl, Map<String, String> params) throws UnsupportedEncodingException, MalformedURLException {
        StringBuilder result = new StringBuilder(baseUrl);
        result.append(getParamsString(params));
        return new URL(result.toString());
    }

    public static String getParamsString(Map<String, String> params) throws UnsupportedEncodingException {
        // StringBuilder result = new StringBuilder();
        StringBuilder result = new StringBuilder("?");

        for (Map.Entry<String, String> entry : params.entrySet()) {
            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
            result.append("&");
        }

        String resultString = result.toString();
        return resultString.length() > 0 ? resultString.substring(0, resultString.length() - 1) : resultString;
    }
}
