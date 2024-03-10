package patcher.files_utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.github.shyiko.klob.Glob;

import lombok.Getter;

public class FileVisitor extends SimpleFileVisitor<Path> {
    private Path activeFolder;
    @Getter
    private List<Path> allFiles = new ArrayList<>();
    private List<String> patterns = new ArrayList<>();
    private List<Path> ignoredFiles = new ArrayList<>();
    private Iterator<Path> iterator;
    
    public FileVisitor() {
        this(null);
    }

    public FileVisitor(Path folder) {
        activeFolder = folder;
        init();
    }

    private void init() {
        if (activeFolder != null && Files.exists(Paths.get(activeFolder.toString(), ".psheignore"))) {
            allFiles.clear();
            patterns.clear();
            ignoredFiles.clear();

            File file = new File(activeFolder.toString(), ".psheignore");
            try {
                patterns = Files.readAllLines(Paths.get(file.toURI()));
            } catch (IOException e) {
                e.printStackTrace();
            }

            iterator = Glob.from(patterns.toArray(new String[0])).iterate(activeFolder);

            while (iterator.hasNext())
                ignoredFiles.add(iterator.next());
        }
    }

    public List<Path> walkFileTree(Path folder) {
        activeFolder = folder;
        init();
        try {
            Files.walkFileTree(activeFolder, this);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return allFiles;
    }

    public List<Path> walkFileTree() {
        return walkFileTree(activeFolder);
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        file = file.toAbsolutePath();
        if (!ignoredFiles.contains(file))
            allFiles.add(file);
 
        return FileVisitResult.CONTINUE;
    }
}
