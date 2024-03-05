package patcher.data_utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class DataEncoder {
    public static int getByteSize(Path filePath) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(getFileContent(filePath));
        oos.close();
        return baos.size();
    }
    
    public static String getChecksum(Path filePath) throws IOException, NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(getFileContent(filePath));
        byte[] digest = md.digest();
        return encode(digest).toLowerCase();
    }

    private static byte[] getFileContent(Path filePath) throws IOException {
        return Files.readAllBytes(filePath);
    }
    
    public static String encode(byte[] data) {
        BigInteger bigInteger = new BigInteger(1, data);
        return bigInteger.toString(16);
    }
}
