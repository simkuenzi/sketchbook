package com.github.simkuenzi.sketchbook;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FilesystemSketch implements ValidSketch {
    private final Path path;

    public FilesystemSketch(Path path) {
        this.path = path;
    }

    @Override
    public SketchName getName() {
        return new SketchName() {
            @Override
            public String getValue() {
                return getId().asName();
            }

            @Override
            public NameValidity getValidity() {
                return NameValidity.VALID;
            }
        };
    }

    @Override
    public SketchId getId() {
        return SketchId.forPath(path);
    }

    @Override
    public String getValidName() {
        return getName().getValue();
    }

    @Override
    public String getContent() throws IOException {
        return Files.exists(path) ? Files.readString(path) : "";
    }

    @Override
    public void save(String newContent) throws IOException {
        Files.createDirectories(path.getParent());
        Files.writeString(path, newContent);
    }

    @Override
    public void save(String newName, String newContent, Action<ValidSketch> onSave, Action<ValidSketch> onDuplicate) throws Exception {
        save(newContent);
        Path newPath = path.getParent().resolve(SketchId.forName(newName).asPath());
        if (!path.equals(newPath)) {
            if (!Files.exists(newPath)) {
                Files.move(path, newPath);
                onSave.apply(new FilesystemSketch(newPath));
            } else {
                onDuplicate.apply(this);
            }
        } else {
            onSave.apply(this);
        }
    }

    @Override
    public void delete() throws IOException {
        Files.deleteIfExists(path);
    }
}
