package patcher.data_utils;
import java.io.IOException;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;

public class IntegrityChecker {
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