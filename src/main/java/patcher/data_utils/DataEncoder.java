package patcher.data_utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class DataEncoder {
    
    public static byte[] decode(String data) {
        return Base64.getDecoder().decode(data);
    }
    public static String encode(byte[] data) {
        return Base64.getEncoder().encodeToString(data);
    }

    public static String encodeChecksum(String toEncode) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA3-256");
        byte[] bytes = digest.digest(toEncode.getBytes());
        
        return encode(bytes);
    }
    public static String encodeChecksum(byte[] toEncode) throws NoSuchAlgorithmException{
        return encodeChecksum(encode(toEncode));
    }
}
