package model;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Model {

    private File folder;
    private List<Path> files;
    private int entries = 0;
    private List<List<Path>> grps = new ArrayList<>();

    public void setFolder(File folder) {
        this.folder = folder;
        if (folder == null) {
            files = null;
            grps = new ArrayList<>();
            return;
        }
        loadFiles();
    }

    private void loadFiles() {
        try (Stream<Path> paths = Files.walk(folder.toPath())) {
            files = paths.filter(Files::isRegularFile).collect(Collectors.toList());
            entries = files.size();
            files.sort(Comparator.naturalOrder());
        } catch (IOException e) {
            e.printStackTrace();
        }
        int chunkSize = entries/6;
        int rest = entries%6;
        for (int i = 0; i < rest; i++) {
            int listStart = i * (chunkSize+1);
            int listEnd = (chunkSize+1) + (i * (chunkSize+1));
            grps.add(i, files.subList(listStart,listEnd));
        }
        for (int i = 0; i < 6 - rest; i++) {
            int listStart = ((chunkSize+1)*rest) + (i*chunkSize);
            int listEnd = chunkSize + (((chunkSize+1)*rest) + (i*chunkSize));
            grps.add((i+rest),files.subList(listStart,listEnd));
        }
    }

    public List<List<Path>> getFiles() {
        return grps;
    }

    public int getEntries() {
        return entries;
    }
}
