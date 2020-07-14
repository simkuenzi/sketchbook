package com.github.simkuenzi.sketchbook;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Sketch {
    private final Path path;

    public Sketch(Path path) {
        this.path = path;
    }

    public String getName() {
        return path.getFileName().toString();
    }

    public String getContent() throws IOException {
        return Files.exists(path) ? Files.readString(path) : "";
    }

    void save(String newContent) throws IOException {
        Files.createDirectories(path.getParent());
        Files.writeString(path, newContent);
    }

    public void delete() throws IOException {
        Files.deleteIfExists(path);
    }
}
