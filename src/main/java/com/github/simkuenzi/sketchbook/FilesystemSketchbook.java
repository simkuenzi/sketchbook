package com.github.simkuenzi.sketchbook;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.Collator;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class FilesystemSketchbook implements Sketchbook {
    private final Path path;

    public FilesystemSketchbook(Path path) {
        this.path = path;
    }

    @Override
    public List<ValidSketch> getSketches() throws IOException {
        return Files.exists(path)
                ? Files.list(path).map(FilesystemSketch::new).sorted(Comparator.comparing(ValidSketch::getValidName, Collator.getInstance())).collect(Collectors.toList())
                : Collections.emptyList();
    }

    @Override
    public ValidSketch sketch(String sketchName) {
        return new FilesystemSketch(path.resolve(Path.of(sketchName)));
    }

    @Override
    public ValidSketch newSketch(String baseName) {
        return IntStream.range(1, 1000)
                .mapToObj(i -> path.resolve(String.format("%s %d", baseName, i)))
                .filter(p -> !Files.exists(p))
                .map(FilesystemSketch::new)
                .findFirst()
                .orElseThrow(() -> new RuntimeException(String.format("Too many sketches with baseName %s.", baseName)));
    }
}
