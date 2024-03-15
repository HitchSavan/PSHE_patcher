package patcher.utils.data_utils;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class DataEncoder {
    public static long getByteSize(Path filePath) throws IOException {
        return filePath.toFile().length();
    }
    
    public static String getChecksum(byte[] filecontent) throws IOException, NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(filecontent);
        byte[] digest = md.digest();
        String checksum = toHexString(digest).toLowerCase();
        return "0".repeat(32 - checksum.length()) + checksum;
    }
    public static String getChecksum(Path filePath) throws IOException, NoSuchAlgorithmException {
        return getChecksum(getFileContent(filePath));
    }

    public static byte[] getFileContent(Path filePath) throws IOException {
        return Files.readAllBytes(filePath);
    }
    
    private static String toHexString(byte[] data) {
        BigInteger bigInteger = new BigInteger(1, data);
        return bigInteger.toString(16);
    }

    public static String toString(byte[] data) {
        return new String(data, StandardCharsets.UTF_8);
    }
}
