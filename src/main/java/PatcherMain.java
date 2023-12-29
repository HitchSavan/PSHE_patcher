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

        System.out.println("_____________DIFF UPDATE OUTPUT_____________");
        updateResult.forEach(item -> {
            System.out.print(item.operation);
            System.out.print(" ");
            System.out.print(item.operation.ordinal());
            System.out.print(" ");
            System.out.println(item.text);
        });

        System.out.println("_____________PATCHER OUTPUT_____________");
        updatePatch.forEach(item -> {
            System.out.println(item);
        });

        System.out.println("_____________PATCH TO TEXT_____________");
        DiffMatchPatch patcher = new DiffMatchPatch();
        System.out.println(patcher.patchToText(updatePatch));

        System.out.println("_____________PATCHED FILETEXT_____________");
        String patchedText = "";

        if (useTxtFormat) {
            patchedText = (String)Patcher.applyPatch(oldData, updatePatch)[0];
            System.out.println(patchedText);
        } else {
            patchedText = (String)Patcher.applyPatch(oldByteData, updatePatch)[0];
            System.out.print("Encoded text: ");
            System.out.println(patchedText);
            System.out.print("Decoded text: ");
            System.out.println(new String(DataEncoder.decode(patchedText), StandardCharsets.UTF_8));
        }

        System.out.println("_____________CHECKSUM_____________");
        System.out.println(IntegrityChecker.check(patchedText, newCheckSum));

        // for byte arrays:
        // _____________DIFF UPDATE OUTPUT_____________
        // EQUAL 2 VGhpcyBpcyB0ZXN0IGZpbGUsDQp0aGUgb
        // DELETE 0 2xk
        // INSERT 1 mV3
        // EQUAL 2 IG9uZS4NC
        // INSERT 1 tGC0L7Rh9C60LANC
        // EQUAL 2 lRoaXMgbGluZSB
        // DELETE 0 vZi
        // INSERT 1 pcy
        // EQUAL 2 B0
        // DELETE 0 ZXh0IHdpb
        // INSERT 1 a
        // EQUAL 2 G
        // DELETE 0 wgYm
        // EQUAL 2 Ugc
        // DELETE 0 mV
        // INSERT 1 2F
        // EQUAL 2 t
        // DELETE 0 b3
        // EQUAL 2 Z
        // INSERT 1 S4NC
        // EQUAL 2 l
        // DELETE 0 ZA0K0YLQvtGH0LrQsA0KVGhpcyBs
        // INSERT 1 RoaXMg
        // EQUAL 2 a
        // INSERT 1 XMgY
        // EQUAL 2 W
        // DELETE 0 5lI
        // INSERT 1 RkaXRpb24gb
        // EQUAL 2 Gl
        // DELETE 0 zIHRo
        // INSERT 1 u
        // EQUAL 2 ZS
        // DELETE 0 BzYW1l
        // INSERT 1 wgd293
        // EQUAL 2 Lg==
        // _____________PATCHER OUTPUT_____________
        // @@ -30,11 +30,11 @@
        // GUgb
        // -2xk
        // +mV3
        // IG9u

        // @@ -38,16 +38,32 @@
        // G9uZS4NC
        // +tGC0L7Rh9C60LANC
        // lRoaXMgb

        // @@ -72,85 +72,61 @@
        // uZSB
        // -vZi
        // +pcy
        // B0
        // -ZXh0IHdpb
        // +a
        // G
        // -wgYm
        // Ugc
        // -mV
        // +2F
        // t
        // -b3
        // Z
        // +S4NC
        // l
        // -ZA0K0YLQvtGH0LrQsA0KVGhpcyBs
        // +RoaXMg
        // a
        // +XMgY
        // W
        // -5lI
        // +RkaXRpb24gb
        // Gl
        // -zIHRo
        // +u
        // ZS
        // -BzYW1l
        // +wgd293
        // Lg==
        // _____________PATCH TO TEXT_____________
        // @@ -30,11 +30,11 @@
        // GUgb
        // -2xk
        // +mV3
        // IG9u
        // @@ -38,16 +38,32 @@
        // G9uZS4NC
        // +tGC0L7Rh9C60LANC
        // lRoaXMgb
        // @@ -72,85 +72,61 @@
        // uZSB
        // -vZi
        // +pcy
        // B0
        // -ZXh0IHdpb
        // +a
        // G
        // -wgYm
        // Ugc
        // -mV
        // +2F
        // t
        // -b3
        // Z
        // +S4NC
        // l
        // -ZA0K0YLQvtGH0LrQsA0KVGhpcyBs
        // +RoaXMg
        // a
        // +XMgY
        // W
        // -5lI
        // +RkaXRpb24gb
        // Gl
        // -zIHRo
        // +u
        // ZS
        // -BzYW1l
        // +wgd293
        // Lg==

        // _____________PATCHED FILETEXT_____________
        // Encoded text: VGhpcyBpcyB0ZXN0IGZpbGUsDQp0aGUgbmV3IG9uZS4NCtGC0L7Rh9C60LANClRoaXMgbGluZSBpcyB0aGUgc2FtZS4NClRoaXMgaXMgYWRkaXRpb24gbGluZSwgd293Lg==
        // Decoded text: This is test file,
        // the new one.
        // точка
        // This line is the same.
        // This is addition line, wow.
    }
}
