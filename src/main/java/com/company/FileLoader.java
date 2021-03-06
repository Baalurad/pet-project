package com.company;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class FileLoader {

    public File getFileFromURL(String path) {
        URL url = this.getClass().getClassLoader().getResource(path);
        File file = null;
        try {
            file = new File(url.toURI());
        } catch (URISyntaxException e) {
            file = new File(url.getPath());
        } finally {
            return file;
        }
    }

    public static List<String> readFromFile(File file) throws IOException {
        ArrayList<String> result = new ArrayList<>();
        try (Stream<String> stream = Files.lines(Paths.get(file.getAbsolutePath()))) {
            stream.forEach((result::add));
        }
        return result;
    }

    public Map<String, Integer> parseFile(String path) throws IOException {
        Map<String, Integer> result = new HashMap<>();
        File file = getFileFromURL(path);
        List<String> rows = readFromFile(file);
        rows.forEach(row -> result.put(
                row.split(" ")[0],
                Integer.parseInt(row.split(" ")[1])
        ));
        return result;
    }
}