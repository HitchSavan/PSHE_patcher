package patcher.utils.data_utils;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;

import patcher.remote_api.endpoints.FilesEndpoint;
import patcher.remote_api.endpoints.VersionsEndpoint;
import patcher.remote_api.entities.VersionEntity;
import patcher.remote_api.entities.VersionFileEntity;

public class IntegrityChecker {
    public static Map<String, ArrayList<Path>> checkRemoteIntegrity(
            Map<Path, Path> patchedFiles, Path oldProjectPath, Map<Path, VersionFileEntity> versionFiles) throws IOException {
        ArrayList<Path> failedFiles = new ArrayList<>();
        ArrayList<Path> missingFiles = new ArrayList<>();
        ArrayList<Path> deletedFiles = new ArrayList<>();
        ArrayList<Path> unchangedFiles = new ArrayList<>();
        patchedFiles.forEach((relativeFile, file) -> {
            System.err.print("checking ");
            System.err.println(file);
            try {
                if (!versionFiles.containsKey(relativeFile)) {
                    deletedFiles.add(file);
                } else {
                    if (DataEncoder.getByteSize(file) == versionFiles.get(relativeFile).getSize()) {
                        if (!compareChecksum(file, versionFiles.get(relativeFile).getChecksum())) {
                            System.out.print("Failed checksum (patched) ");
                            System.out.print(DataEncoder.getChecksum(file));
                            System.out.print(" ");
                            System.out.println(versionFiles.get(relativeFile).getChecksum());
                            failedFiles.add(file);
                        }
                    } else {
                        System.out.print("Failed filesize (patched) ");
                        System.out.print(DataEncoder.getByteSize(file));
                        System.out.print(" ");
                        System.out.println(versionFiles.get(relativeFile).getSize());
                        failedFiles.add(file);
                    }
                }
            } catch (IOException | NoSuchAlgorithmException | JSONException e) {
                e.printStackTrace();
            }
        });

        versionFiles.keySet().forEach(remoteFile -> {
            Path file = Paths.get(oldProjectPath.toString(), remoteFile.toString());
            System.err.print("checking remote ");
            System.err.println(remoteFile);
            if (!patchedFiles.containsKey(remoteFile)) {
                if (Files.exists(file)) {
                    try {
                        if (DataEncoder.getByteSize(file) == versionFiles.get(remoteFile).getSize()) {
                            if (!compareChecksum(file, versionFiles.get(remoteFile).getChecksum())) {
                                System.out.print("Failed checksum (old) ");
                                System.out.print(DataEncoder.getChecksum(file));
                                System.out.print(" ");
                                System.out.println(versionFiles.get(remoteFile).getChecksum());
                                missingFiles.add(remoteFile);
                            } else {
                                unchangedFiles.add(file);
                            }
                        } else {
                            System.out.print("Failed filesize (old) ");
                            System.out.print(DataEncoder.getByteSize(file));
                            System.out.print(" ");
                            System.out.println(versionFiles.get(remoteFile).getSize());
                            missingFiles.add(remoteFile);
                        }
                    } catch (NoSuchAlgorithmException | IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    missingFiles.add(remoteFile);
                }
            }
        });

        Map<String, ArrayList<Path>> result = new HashMap<>(
            Map.of("failed", failedFiles,
                    "missing", missingFiles,
                    "deleted", deletedFiles,
                    "unchanged", unchangedFiles));

        return result;
    }
    public static Map<String, ArrayList<Path>> checkRemoteIntegrity(Path localFile, Path oldProjectPath, Path newProjectPath, String version) throws IOException {
        Path relativeFile = newProjectPath.relativize(localFile);
        return checkRemoteIntegrity(new HashMap<Path, Path>(Map.of(relativeFile, localFile)), oldProjectPath,
                new HashMap<Path, VersionFileEntity>(Map.of(relativeFile,
                        new VersionFileEntity(FilesEndpoint.getVersion(Map.of("location", relativeFile.toString(), "v", version)), relativeFile))));
    }
    public static Map<String, ArrayList<Path>> checkRemoteIntegrity(Map<Path, Path> patchedFiles, Path oldProjectPath, String version) throws IOException {
        return checkRemoteIntegrity(patchedFiles, oldProjectPath,
                new VersionEntity(VersionsEndpoint.getVersions(Map.of("v", version)).getJSONObject("version")).getFiles());
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
    public static boolean compareChecksum(String fileChecksum, Path checksum) throws NoSuchAlgorithmException, IOException {
        return compareChecksum(checksum, fileChecksum);
    }
    public static boolean compareChecksum(Path file, Path checksum) throws NoSuchAlgorithmException, IOException {
        return compareChecksum(DataEncoder.getChecksum(file), DataEncoder.getChecksum(checksum));
    }
    public static boolean compareChecksum(byte[] filecontent, Path checksum) throws NoSuchAlgorithmException, IOException {
        return compareChecksum(DataEncoder.getChecksum(filecontent), DataEncoder.getChecksum(checksum));
    }
    public static boolean compareChecksum(String fileChecksum, byte[] checksum) throws NoSuchAlgorithmException, IOException {
        return compareChecksum(DataEncoder.getChecksum(checksum), fileChecksum);
    }
    public static boolean compareChecksum(Path file, byte[] checksum) throws NoSuchAlgorithmException, IOException {
        return compareChecksum(DataEncoder.getChecksum(file), DataEncoder.getChecksum(checksum));
    }
    public static boolean compareChecksum(byte[] filecontent, byte[] checksum) throws NoSuchAlgorithmException, IOException {
        return compareChecksum(DataEncoder.getChecksum(filecontent), DataEncoder.getChecksum(checksum));
    }
}