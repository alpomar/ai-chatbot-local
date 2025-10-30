package dev.fp.aichatbot.rag;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileManager {

    public static List<File> getFiles(String rootFolder) {
        List<File> filesList = new ArrayList<>();
        File folder = new File(rootFolder);

        if (!folder.exists() || !folder.isDirectory()) {
            System.err.println("Invalid root folder" + rootFolder);
            return null;
        }

        File[] files = folder.listFiles();
        if (files == null) {
            System.err.println("No files in folder: " + rootFolder);
            return null;
        }

        for (File file : files) {
            if (!file.getName().startsWith(".")) {
                filesList.add(file);
            }
        }

        return filesList;
    }

}
