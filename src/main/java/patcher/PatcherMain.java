package patcher;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import patcher.remote_api.endpoints.FilesEndpoint;
import patcher.remote_api.endpoints.VersionsEndpoint;
import patcher.remote_api.entities.VersionEntity;
import patcher.utils.data_utils.DataEncoder;
import patcher.utils.data_utils.IntegrityChecker;
import patcher.utils.files_utils.FileVisitor;
import patcher.utils.remote_utils.Connector;

public class PatcherMain {
    public static void main(String[] args) throws IOException {
        Map<String, String> parameters = new HashMap<>();

        Connector.setBaseUrl("http://tarkov.deadlauncher.fun");
        Path projectPath = Paths.get("D:\\projects\\Jabix\\pshe\\PSHE_user_client\\test files\\game — копия_tmp");
        Path patchedPath = Paths.get("D:\\projects\\Jabix\\pshe\\PSHE_user_client\\test files\\patched_tmp\\game — копия_tmp");

        if (Files.exists(projectPath.resolve("config.json"))) {
            File file = new File(projectPath.resolve("config.json").toString());
            String content;
            try {
                content = new String(Files.readAllBytes(Paths.get(file.toURI())));
                parameters.put("v", new JSONObject(content).getString("currentVersion"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        StringBuilder dump = new StringBuilder();

        JSONObject versionInfo = VersionsEndpoint.getVersions(parameters);
        VersionEntity curVersion = new VersionEntity(versionInfo.getJSONObject("version"));

        VersionEntity rootVersion = new VersionEntity(versionInfo.getJSONObject("version"));

        AtomicInteger counter = new AtomicInteger(0);

        FileVisitor fileVisitor = new FileVisitor(projectPath);
        Map<Path, Path> localFiles = new HashMap<>();
        fileVisitor.walkFileTree().forEach(filePath -> {
            if (counter.getAndIncrement() % 50 == 0)
                System.out.println(counter.get());
                localFiles.put(projectPath.relativize(filePath), filePath);
        });
        
        Map<Path, Path> patchedFiles = new HashMap<>();
        fileVisitor.walkFileTree(patchedPath).forEach(filePath -> {
            if (counter.getAndIncrement() % 50 == 0)
                System.out.println(counter.get());
                patchedFiles.put(patchedPath.relativize(filePath), filePath);
        });

        try {
            JSONObject versionsHistory = VersionsEndpoint.getHistory();

            if (VersionsEndpoint.getHistory().getBoolean("success")) {
                for (Object v: versionsHistory.getJSONArray("versions")) {
                    if (((JSONObject)v).getBoolean("is_root")) {
                        rootVersion = new VersionEntity(((JSONObject)v).put("files", new JSONArray()));
                        break;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        IntegrityChecker.checkRemoteIntegrity(patchedFiles, projectPath, rootVersion.getVersionString());

        // fileVisitor.walkFileTree().forEach(filePath -> {
        //     if (counter.getAndIncrement() % 50 == 0)
        //         System.out.println(counter.get());
        //     if (!filePath.endsWith(".psheignore")) {
        //         localFiles.put(projectPath.relativize(filePath), filePath);
        //         if (!version.getFiles().keySet().contains(projectPath.relativize(filePath))) {
        //             dump.append("Not found in remote: ");
        //             dump.append(projectPath.relativize(filePath).toString());
        //             dump.append(System.lineSeparator());
        //         }
        //     }
        // });
        // System.out.println("Checksums");
        // counter.set(0);
        // version.getFiles().keySet().forEach(item -> {
        //     if (counter.getAndIncrement() % 50 == 0)
        //         System.out.println(Double.valueOf(counter.get()) / version.getFiles().keySet().size() * 100);
        //     if (!localFiles.keySet().contains(item)) {
        //         dump.append("Not found in local: ");
        //         dump.append(item);
        //         dump.append(System.lineSeparator());
        //     } else {
        //         try {
        //             String localChecksum = DataEncoder.getChecksum(localFiles.get(item));
        //             if (!IntegrityChecker.compareChecksum(localChecksum, version.getFiles().get(item).getChecksum())) {
        //                 Map<String, String> fileParam = new HashMap<>();
        //                 fileParam.put("location", version.getFiles().get(item).getLocation().toString());
        //                 JSONArray versions = FilesEndpoint.getFiles(fileParam).getJSONObject("file").getJSONArray("versions");
        //                 AtomicBoolean checksumInOtherVersion = new AtomicBoolean(false);
        //                 versions.forEach(vers -> {
        //                     try {
        //                         if (IntegrityChecker.compareChecksum(localChecksum, ((JSONObject)vers).getString("checksum"))) {
        //                             checksumInOtherVersion.set(true);
        //                         }
        //                     } catch (NoSuchAlgorithmException | JSONException | IOException e) {
        //                         e.printStackTrace();
        //                     }
        //                 });
        //                 if (!checksumInOtherVersion.get()) {
        //                     dump.append("Checksum mismatch for: ");
        //                     dump.append(item);
        //                     dump.append(System.lineSeparator());
        //                     dump.append("\tlocal:\t");
        //                     dump.append(localChecksum);
        //                     dump.append(System.lineSeparator());
        //                     dump.append("\tremote:\t");
        //                     dump.append(version.getFiles().get(item).getChecksum());
        //                     dump.append(System.lineSeparator());
        //                 } else {
        //                     dump.append("Checksum found on other version for: ");
        //                     dump.append(item);
        //                     dump.append(System.lineSeparator());
        //                 }
        //             }
        //         } catch (NoSuchAlgorithmException | IOException e) {
        //             e.printStackTrace();
        //         }
        //     }
        // });

        // BufferedWriter writer = new BufferedWriter(new FileWriter("dump.txt"));
        // writer.write(dump.toString());
        // writer.close();
    }
}
