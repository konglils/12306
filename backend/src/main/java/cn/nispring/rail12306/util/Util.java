package cn.nispring.rail12306.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Util {

    public static List<String[]> readCsv(Path path) throws IOException {
        if (!Files.isRegularFile(path)) {
            throw new IllegalStateException("not a file: " + path);
        }
        List<String[]> rows = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String line;
            boolean header = true;
            while ((line = reader.readLine()) != null) {
                if (header) {
                    header = false;
                    continue;
                }
                if (line.isEmpty()) {
                    continue;
                }
                rows.add(line.split(",", -1));
            }
        }
        return rows;
    }
}
