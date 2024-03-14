package patcher.utils.data_utils;
import java.io.IOException;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;

import patcher.remote_api.endpoints.VersionsEndpoint;
import patcher.remote_api.entities.VersionEntity;

public class IntegrityChecker {
    public static Map<String, ArrayList<Path>> checkRemoteIntegrity(ArrayList<Path> localFiles, String version) throws IOException {
        VersionEntity versionInfo = new VersionEntity(VersionsEndpoint.getVersions(Map.of("v", version)).getJSONObject("version"));

        ArrayList<Path> failedFiles = new ArrayList<>();
        ArrayList<Path> deletedFiles = new ArrayList<>();
        localFiles.forEach(file -> {
            try {
                if (!versionInfo.getFiles().containsKey(file)) {
                    deletedFiles.add(file);
                } else {
                    if (DataEncoder.getByteSize(file) == versionInfo.getFiles().get(file).getSize()) {
                        if (!compareChecksum(file, versionInfo.getFiles().get(file).getChecksum())) {
                            failedFiles.add(file);
                        }
                    } else {
                        failedFiles.add(file);
                    }
                }
            } catch (IOException | NoSuchAlgorithmException | JSONException e) {
                e.printStackTrace();
            }
        });

        versionInfo.getFiles().keySet().forEach(remoteFile -> {
            if (!localFiles.contains(remoteFile)) {
                failedFiles.add(remoteFile);
            }
        });

        Map<String, ArrayList<Path>> result = new HashMap<>(
            Map.of("failed", failedFiles,
                    "deleted", deletedFiles));

        return result;
    }
    public static boolean checkRemoteIntegrity(Path localFile, String version) throws IOException {
        return checkRemoteIntegrity(new ArrayList<Path>(List.of(localFile)), version).isEmpty();
    }

    public static boolean compareChecksum(String fileChecksum, String checksum) throws NoSuchAlgorithmException, IOException {
        return fileChecksum.equals(checksum);
    }
    public static boolean compareChecksum(Path file, String checksum) throws NoSuchAlgorithmException, IOException {
        return compareChecksum(DataEncoder.getChecksum(file), checksum);
    }
    public static boolean compareChecksum(byte[] filecontent, String checksum) throws NoSuchAlgorithmException, IOException {
        return compareChecksum(DataEncoder.getChecksum(filecontent), checksum);
    }
}