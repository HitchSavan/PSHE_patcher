import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;

import org.bitbucket.cowwoc.diffmatchpatch.DiffMatchPatch;
import org.json.JSONArray;
import org.json.JSONObject;

import patching.DataEncoder;
import patching.IntegrityChecker;
import patching.PatchCreator;
import patching.Patcher;

public class PatcherMain {

    private static int getByteSize(Object object) throws IOException {
        ByteArrayOutputStream baos=new ByteArrayOutputStream();
        ObjectOutputStream oos=new ObjectOutputStream(baos);
        oos.writeObject(object);
        oos.close();
        return baos.size();
    }

    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
        try {
            Files.createDirectories(Paths.get("output"));
        } catch (IOException e) {
            System.err.println("Cannot create directories - " + e);
        }
        boolean useTxtFormat = false;

        byte[] oldByteData = new byte[0];
        byte[] newByteData = new byte[0];

        switch (args.length) {
            case 0:
                System.out.println("You can pass paths to old and new files through args");
                oldByteData = PatcherMain.class.getResourceAsStream("/test_old.txt").readAllBytes();
                newByteData = PatcherMain.class.getResourceAsStream("/test_new.txt").readAllBytes();
                break;
            case 2:
                oldByteData = Files.readAllBytes(Paths.get(args[0]));
                newByteData = Files.readAllBytes(Paths.get(args[1]));
                break;
            default:
                System.out.println("Pass paths to old and new files through args");
                System.exit(1);
        }

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
            newData = DataEncoder.encode(newByteData);
        }

        JSONArray filePatch = PatchCreator.getCustomPatch(oldData, newData);

        stringBuffer.append("\n_____________CUSTOM PATCH JSON_____________\n");
        stringBuffer.append(filePatch);
        stringBuffer.append("\n_____________CUSTOM PATCHED STR_____________\n");
        String customPatchedText = Patcher.applyCustomPatch(oldData, filePatch);
        stringBuffer.append(customPatchedText);
        stringBuffer.append("\n_____________REVERSE CUSTOM PATCHED STR_____________\n");
        stringBuffer.append(Patcher.revertCustomPatch(customPatchedText, filePatch));

        stringBuffer.append("\n_____________OLD FILE BYTES SIZE_____________\n");
        stringBuffer.append(getByteSize(oldData));
        stringBuffer.append("\n_____________NEW FILE BYTES SIZE_____________\n");
        stringBuffer.append(getByteSize(newData));
        stringBuffer.append("\n_____________OLD PATCH BYTES SIZE_____________\n");
        stringBuffer.append(getByteSize(patchText));
        stringBuffer.append("\n_____________NEW PATCH BYTES SIZE_____________\n");
        stringBuffer.append(getByteSize(filePatch.toList()));

        FileOutputStream outputStream = new FileOutputStream("output\\outinfo.txt");
        byte[] strToBytes = stringBuffer.toString().getBytes();
        outputStream.write(strToBytes);
        outputStream.close();

        System.out.println("\nPress enter to close...");

        JSONObject patchFile = new JSONObject().put("patch", filePatch);
        if (args.length > 0) {
            patchFile.put("filename", args[0]);
        } else {
            patchFile.put("filename", "test_old.txt");
        }

        JSONObject compressedJson = PatchCreator.compressJson(patchFile);

        FileOutputStream jsonOutputStream = new FileOutputStream("output\\lil_patch.json");
        jsonOutputStream.write(compressedJson.toString().getBytes());
        jsonOutputStream.close();

        jsonOutputStream = new FileOutputStream("output\\patch.json");
        jsonOutputStream.write(PatchCreator.decompressJson(compressedJson).toString(4).getBytes());
        jsonOutputStream.close();

        System.in.read();
    }
}
