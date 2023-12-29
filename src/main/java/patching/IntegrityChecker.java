package patching;
import java.security.NoSuchAlgorithmException;

public class IntegrityChecker {

    public static boolean check(String toEncode, String givenChecksum) throws NoSuchAlgorithmException {
        String currentChecksum = DataEncoder.encodeChecksum(toEncode);

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

    public static boolean check(byte[] toEncode, String givenChecksum) throws NoSuchAlgorithmException {
        return check(DataEncoder.encode(toEncode), givenChecksum);
    }
}