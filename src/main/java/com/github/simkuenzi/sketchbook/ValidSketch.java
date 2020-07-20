package com.github.simkuenzi.sketchbook;

import java.io.IOException;

public interface ValidSketch extends Sketch {
    SketchId getId();

    String getValidName();

    void save(String newContent) throws IOException;

    void save(String newName, String newContent, Action<ValidSketch> onSave, Action<ValidSketch> onDuplicate) throws Exception;

    void delete() throws IOException;
}
