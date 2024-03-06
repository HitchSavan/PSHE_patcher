package patcher;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import patcher.remote_api.endpoints.Versions;

public class PatcherMain {
    public static void main(String[] args) throws IOException {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("v", "0.14.1.1.28875");

        System.out.println(Versions.getHistory());
    }
}
