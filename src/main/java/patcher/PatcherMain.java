package patcher;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import patcher.remote_api.endpoints.Service;

public class PatcherMain {
    public static void main(String[] args) throws IOException {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("v", "0.14.1.1.28875");

        System.out.println(Service.ping());
    }
}
