package patcher.utils.data_utils;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;

import patcher.remote_api.endpoints.FilesEndpoint;
import patcher.remote_api.endpoints.VersionsEndpoint;
import patcher.remote_api.entities.VersionEntity;
import patcher.remote_api.entities.VersionFileEntity;

public class IntegrityChecker {
    private static void checkLocalIntegrity(Path file, Path relativeFile,
            List<Path> failedFiles, List<Path> deletedFiles, Map<Path, VersionFileEntity> versionFiles, StringBuffer integrityDump) {
        try {
            if (!versionFiles.containsKey(relativeFile)) {
                integrityDump.append("Not found in remote (deleted) ").append(file).append(System.lineSeparator());
                deletedFiles.add(file);
            } else {
                if (DataEncoder.getByteSize(file) == versionFiles.get(relativeFile).getSize()) {
                    if (!compareChecksum(file, versionFiles.get(relativeFile).getChecksum())) {
                        integrityDump.append("Failed checksum (patched) ")
                                .append(file)
                                .append(" local: ")
                                .append(DataEncoder.getChecksum(file))
                                .append(" remote: ")
                                .append(versionFiles.get(relativeFile).getChecksum())
                                .append(System.lineSeparator());
                        System.out.print("Failed checksum (patched) ");
                        System.out.print(DataEncoder.getChecksum(file));
                        System.out.print(" ");
                        System.out.println(versionFiles.get(relativeFile).getChecksum());
                        failedFiles.add(file);
                    }
                } else {
                    integrityDump.append("Failed filesize (patched) ")
                            .append(file)
                            .append(" local: ")
                            .append(DataEncoder.getByteSize(file))
                            .append(" remote: ")
                            .append(versionFiles.get(relativeFile).getSize())
                            .append(System.lineSeparator());
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
    }

    private static void checkRemoteIntegrity(Path file, Path remoteFile, Map<Path, Path> patchedFiles,
            List<Path> unchangedFiles, List<Path> missingFiles, Map<Path, VersionFileEntity> versionFiles, StringBuffer integrityDump) {
        if (!patchedFiles.containsKey(remoteFile)) {
            integrityDump.append("Remote not found in local ")
                    .append(file)
                    .append(System.lineSeparator());
            if (Files.exists(file)) {
                integrityDump.append("\tRemote found in old project")
                        .append(System.lineSeparator());
                try {
                    if (DataEncoder.getByteSize(file) == versionFiles.get(remoteFile).getSize()) {
                        if (!compareChecksum(file, versionFiles.get(remoteFile).getChecksum())) {
                            integrityDump.append("\t\tFailed checksum (old) ")
                                    .append(" local: ")
                                    .append(DataEncoder.getChecksum(file))
                                    .append(" remote: ")
                                    .append(versionFiles.get(remoteFile).getChecksum())
                                    .append(System.lineSeparator());
                            System.out.print("Failed checksum (old) ");
                            System.out.print(DataEncoder.getChecksum(file));
                            System.out.print(" ");
                            System.out.println(versionFiles.get(remoteFile).getChecksum());
                            missingFiles.add(remoteFile);
                        } else {
                            integrityDump.append("\t\tFile unchanged (copied from old)")
                                    .append(System.lineSeparator());
                            unchangedFiles.add(file);
                        }
                    } else {
                        integrityDump.append("\t\tFailed filesize (old) ")
                                .append(" local: ")
                                .append(DataEncoder.getByteSize(file))
                                .append(" remote: ")
                                .append(versionFiles.get(remoteFile).getSize())
                                .append(System.lineSeparator());
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
                integrityDump.append("\tRemote not found in old project (added)")
                        .append(System.lineSeparator());
                missingFiles.add(remoteFile);
            }
        }
    }
    public static void checkRemoteIntegrity(Path file, Path remoteFile, Map<Path, Path> patchedFiles,
            List<Path> unchangedFiles, List<Path> missingFiles, Map<Path, VersionFileEntity> versionFiles) {
        checkRemoteIntegrity(file, remoteFile, patchedFiles, unchangedFiles, missingFiles, versionFiles, new StringBuffer());
    }
    public static void checkLocalIntegrity(Path file, Path relativeFile,
            List<Path> failedFiles, List<Path> deletedFiles, Map<Path, VersionFileEntity> versionFiles) {
        checkLocalIntegrity(file, relativeFile, failedFiles, deletedFiles, versionFiles, new StringBuffer());
    }

    public static Map<String, List<Path>> checkProjectIntegrity(
            Map<Path, Path> patchedFiles, Path oldProjectPath, Map<Path, VersionFileEntity> versionFiles) throws IOException {
        List<Path> failedFiles = new ArrayList<>();
        List<Path> missingFiles = new ArrayList<>();
        List<Path> deletedFiles = new ArrayList<>();
        List<Path> unchangedFiles = new ArrayList<>();

        StringBuffer integrityDump = new StringBuffer();

        patchedFiles.forEach((relativeFile, file) -> {
            checkLocalIntegrity(file, relativeFile, failedFiles, deletedFiles, versionFiles, integrityDump);
        });

        versionFiles.keySet().forEach(remoteFile -> {
            Path file = oldProjectPath.resolve(remoteFile.toString());
            checkRemoteIntegrity(file, remoteFile, patchedFiles, unchangedFiles, missingFiles, versionFiles, integrityDump);
        });

        integrityDump.append("Total checked files: ")
                .append(failedFiles.size() + missingFiles.size() + deletedFiles.size() + unchangedFiles.size())
                .append(System.lineSeparator())
                .append("Total failed files: ")
                .append(failedFiles.size())
                .append(System.lineSeparator())
                .append("Total missing files: ")
                .append(missingFiles.size())
                .append(System.lineSeparator())
                .append("Total deleted files: ")
                .append(deletedFiles.size())
                .append(System.lineSeparator())
                .append("Total unchanged files: ")
                .append(unchangedFiles.size());
                
        BufferedWriter writer = new BufferedWriter(new FileWriter("integrity_dump.txt"));
        writer.write(integrityDump.toString());
        writer.close();

        Map<String, List<Path>> result = new HashMap<>(
            Map.of("failed", failedFiles,
                    "missing", missingFiles,
                    "deleted", deletedFiles,
                    "unchanged", unchangedFiles));

        return result;
    }
    public static Map<String, List<Path>> checkProjectIntegrity(Path localFile, Path oldProjectPath, Path newProjectPath, String version) throws IOException {
        Path relativeFile = newProjectPath.relativize(localFile);
        return checkProjectIntegrity(new HashMap<Path, Path>(Map.of(relativeFile, localFile)), oldProjectPath,
                new HashMap<Path, VersionFileEntity>(Map.of(relativeFile,
                        new VersionFileEntity(FilesEndpoint.getVersion(Map.of("location", relativeFile.toString(), "v", version)), relativeFile))));
    }
    public static Map<String, List<Path>> checkProjectIntegrity(Map<Path, Path> patchedFiles, Path oldProjectPath, String version) throws IOException {
        return checkProjectIntegrity(patchedFiles, oldProjectPath,
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