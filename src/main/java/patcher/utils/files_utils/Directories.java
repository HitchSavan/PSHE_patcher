package patcher.utils.files_utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.json.JSONException;
import org.json.JSONObject;

public class Directories {
    public static void unpackResources(String source, Path target) throws IOException {
        InputStream is = Directories.class.getResourceAsStream("/" + source + ".zip");
        ZipInputStream zis = new ZipInputStream(is);

        byte[] buffer = new byte[1024];
        ZipEntry entry;
        File destDir = target.toFile();

        while ((entry = zis.getNextEntry()) != null) {
            File newFile = newFile(destDir, entry);
            if (newFile.isDirectory()) {
                if (!newFile.isDirectory() && !newFile.mkdirs()) {
                    throw new IOException("Failed to create directory " + newFile);
                }
            } else {
                // fix for Windows-created archives
                File parent = newFile.getParentFile();
                if (!parent.isDirectory() && !parent.mkdirs()) {
                    throw new IOException("Failed to create directory " + parent);
                }

                FileOutputStream fos = new FileOutputStream(newFile);
                int len;
                while ((len = zis.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }
                fos.close();
            }
        }
    }

    private static File newFile(File destinationDir, ZipEntry zipEntry) throws IOException {
        File destFile = new File(destinationDir, zipEntry.getName());
    
        String destDirPath = destinationDir.getCanonicalPath();
        String destFilePath = destFile.getCanonicalPath();
    
        if (!destFilePath.startsWith(destDirPath + File.separator)) {
            throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
        }
    
        return destFile;
    }

    private static boolean deleteDirectory(File directoryToBeDeleted) {
        File[] allContents = directoryToBeDeleted.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                deleteDirectory(file);
            }
        }
        return directoryToBeDeleted.delete();
    }
    public static void deleteDirectory(String directoryToBeDeleted) {
        deleteDirectory(Paths.get(directoryToBeDeleted).toFile());
    }
    public static void deleteDirectory(Path directoryToBeDeleted) {
        deleteDirectory(directoryToBeDeleted.toString());
    }

    public static void saveJSONFile(Path file, JSONObject content) throws JSONException, IOException {
        FileOutputStream jsonOutputStream;
        jsonOutputStream = new FileOutputStream(file.toString());
        jsonOutputStream.write(content.toString(4).getBytes());
        jsonOutputStream.close();
    }
}
