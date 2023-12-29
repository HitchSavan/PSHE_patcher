import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class IntegrityChecker {
    public boolean check(String toEncode, String givenChecksum) throws NoSuchAlgorithmException {
        String currentChecksum = encodeChecksum(toEncode);

        if (!givenChecksum.equals(currentChecksum)) {
            StringBuffer errMessage = new StringBuffer();
            errMessage.append("State hash mismatch:\n\thave\t");
            errMessage.append(currentChecksum);
            errMessage.append("|\n\tgot\t");
            errMessage.append(givenChecksum);
            errMessage.append("|");
            errMessage.append(" ");
            System.err.println(errMessage.toString());
        }
        return currentChecksum.equals(givenChecksum);
    }

    protected String encodeChecksum(String toEncode) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA3-256");
        byte[] bytes = digest.digest(toEncode.getBytes());
        
        return Base64.getEncoder().encodeToString(bytes);
    }
}