import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;

import org.bitbucket.cowwoc.diffmatchpatch.DiffMatchPatch;

import patching.DataEncoder;
import patching.IntegrityChecker;
import patching.PatchCreator;
import patching.Patcher;

public class PatcherMain {

    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
        Path path = Paths.get("output");

        try {
            Files.createDirectories(path);
        } catch (IOException e) {
            System.err.println("Cannot create directories - " + e);
        }

        boolean useTxtFormat = false;

        byte[] oldByteData = PatcherMain.class.getResourceAsStream("/test_old.txt").readAllBytes();
        byte[] newByteData = PatcherMain.class.getResourceAsStream("/test_new.txt").readAllBytes();
        String oldData = "";
        String newData = "";
        String newCheckSum = "";

        LinkedList<DiffMatchPatch.Diff> updateResult = new LinkedList<>();
        LinkedList<DiffMatchPatch.Patch> updatePatch = new LinkedList<>();
        if (useTxtFormat) {
            oldData = new String(oldByteData, StandardCharsets.UTF_8);
            newData = new String(newByteData, StandardCharsets.UTF_8);

            newCheckSum =  DataEncoder.encodeChecksum(newData);
            updateResult = PatchCreator.getDiff(oldData, newData);
            updatePatch = PatchCreator.getPatch(oldData, newData);
        } else {
            newCheckSum =  DataEncoder.encodeChecksum(newByteData);
            updateResult = PatchCreator.getDiff(oldByteData, newByteData);
            updatePatch = PatchCreator.getPatch(oldByteData, newByteData);
        }

        StringBuffer stringBuffer = new StringBuffer();

        stringBuffer.append("_____________OLD BINARY TEXT FILE_____________\n");
        stringBuffer.append(useTxtFormat ? oldData : new String(oldByteData, StandardCharsets.UTF_8));

        stringBuffer.append("\n_____________NEW BINARY TEXT FILE_____________\n");
        stringBuffer.append(useTxtFormat ? newData : new String(newByteData, StandardCharsets.UTF_8));

        stringBuffer.append("\n_____________DIFF UPDATE OUTPUT_____________\n");
        updateResult.forEach(item -> {
            stringBuffer.append(item.operation);
            stringBuffer.append(" ");
            stringBuffer.append(item.operation.ordinal());
            stringBuffer.append(" ");
            stringBuffer.append(item.text);
            stringBuffer.append("\n");
        });

        stringBuffer.append("_____________PATCHER OUTPUT_____________\n");
        updatePatch.forEach(item -> {
            stringBuffer.append(item);
            stringBuffer.append("\n");
        });

        stringBuffer.append("_____________PATCH TO TEXT_____________\n");
        DiffMatchPatch patcher = new DiffMatchPatch();
        stringBuffer.append(patcher.patchToText(updatePatch));
        stringBuffer.append("\n");

        stringBuffer.append("_____________PATCHED FILETEXT_____________\n");
        String patchedText = "";

        if (useTxtFormat) {
            patchedText = (String)Patcher.applyPatch(oldData, updatePatch)[0];
            stringBuffer.append(patchedText);
            stringBuffer.append("\n");
        } else {
            patchedText = (String)Patcher.applyPatch(oldByteData, updatePatch)[0];
            stringBuffer.append("Encoded text: ");
            stringBuffer.append(patchedText);
            stringBuffer.append("\n");
            stringBuffer.append("Decoded text: ");
            stringBuffer.append(new String(DataEncoder.decode(patchedText), StandardCharsets.UTF_8));
            stringBuffer.append("\n");
        }

        stringBuffer.append("_____________CHECKSUM_____________\n");
        stringBuffer.append(newCheckSum).append("\n");
        stringBuffer.append(IntegrityChecker.check(patchedText, newCheckSum));

        FileOutputStream outputStream = new FileOutputStream("output\\outinfo.txt");
        byte[] strToBytes = stringBuffer.toString().getBytes();
        outputStream.write(strToBytes);
        outputStream.close();

        stringBuffer.append("\nInput any key to close...\n");

        System.out.println(stringBuffer.toString());
        System.in.read();
    }
}
