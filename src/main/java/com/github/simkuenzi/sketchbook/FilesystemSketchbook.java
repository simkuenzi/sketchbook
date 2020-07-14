package com.github.simkuenzi.sketchbook;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class FilesystemSketchbook implements Sketchbook {
    private final Path path;

    public FilesystemSketchbook(Path path) {
        this.path = path;
    }

    @Override
    public List<Sketch> getSketches() throws IOException {
        return Files.exists(path)
                ? Files.list(path).map(Sketch::new).collect(Collectors.toList())
                : Collections.emptyList();
    }

    @Override
    public Sketch sketch(String sketchName) {
        return new Sketch(path.resolve(Path.of(sketchName)));
    }
}
