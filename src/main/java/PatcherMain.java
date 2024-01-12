import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.LinkedList;

import org.bitbucket.cowwoc.diffmatchpatch.DiffMatchPatch;
import org.bitbucket.cowwoc.diffmatchpatch.DiffMatchPatch.Diff;

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
        String patchText = patcher.patchToText(updatePatch);
        stringBuffer.append(patchText);
        stringBuffer.append("\n");

        stringBuffer.append("_____________PATCHED FILETEXT_____________\n");
        String patchedText = "";

        if (useTxtFormat) {
            patchedText = (String)Patcher.applyPatch(oldData, patcher.patchFromText(patchText))[0];
            stringBuffer.append(patchedText);
            stringBuffer.append("\n");
        } else {
            patchedText = (String)Patcher.applyPatch(oldByteData, patcher.patchFromText(patchText))[0];
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

        if (!useTxtFormat) {
            oldData = DataEncoder.encode(oldByteData);
        }

        // id, position, mod type, substr
        HashMap<Integer, HashMap<Integer, HashMap<Boolean, String>>> patchMap = new HashMap<>();

        int i = 0;
        int curMod = 0;
        Diff item;
        // try to reduce patch size
        for (int curDiff = 0; curDiff < updateResult.size(); curDiff++) {
            item = updateResult.get(curDiff);

            switch (item.operation.ordinal()) {
                case 2: // EQUAL
                    i += item.text.length();
                    break;
                    
                case 0: // DELETE
                    oldData = oldData.substring(0, i) + oldData.substring(i).replaceFirst(item.text, "");
                    patchMap.put(curMod, new HashMap<>());
                    patchMap.get(curMod).put(i, new HashMap<>());
                    patchMap.get(curMod).get(i).put(false, item.text);
                    curMod += 1;
                    break;

                case 1: // INSERT
                    oldData = oldData.substring(0, i) + item.text + oldData.substring(i);
                    i += item.text.length();
                    patchMap.put(curMod, new HashMap<>());
                    patchMap.get(curMod).put(i, new HashMap<>());
                    patchMap.get(curMod).get(i).put(true, item.text);
                    curMod += 1;
                    break;
            
                default:
                    break;
            }

        }

        stringBuffer.append("\n_____________PATCH_MAP_____________\n");
        stringBuffer.append(patchMap.toString());
        stringBuffer.append("\n_____________HAND-PATCHED_STR_____________\n");
        stringBuffer.append(oldData);

        stringBuffer.append("\n_____________PATCH_SYMBOLS_SIZE_COMPARISON_____________\n");
        stringBuffer.append("Old: ").append(patchText.length());
        stringBuffer.append("\nNew: ").append(patchMap.toString().length());

        ByteArrayOutputStream baos=new ByteArrayOutputStream();
        ObjectOutputStream oos=new ObjectOutputStream(baos);
        oos.writeObject(patchMap);
        oos.close();
        stringBuffer.append("\n_____________NEW_PATCH_BYTES_SIZE_____________\n");
        stringBuffer.append(baos.size());

        FileOutputStream outputStream = new FileOutputStream("output\\outinfo.txt");
        byte[] strToBytes = stringBuffer.toString().getBytes();
        outputStream.write(strToBytes);
        outputStream.close();

        stringBuffer.append("\nInput any key to close...\n");

        System.out.println(stringBuffer.toString());
        System.in.read();
    }
}
