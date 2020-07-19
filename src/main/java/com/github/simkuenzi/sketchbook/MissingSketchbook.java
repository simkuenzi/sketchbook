package com.github.simkuenzi.sketchbook;

import java.util.Collections;
import java.util.List;

public class MissingSketchbook implements Sketchbook {
    @Override
    public List<ValidSketch> getSketches() {
        return Collections.emptyList();
    }

    @Override
    public ValidSketch sketch(String sketchName) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ValidSketch newSketch(String baseName) {
        throw new UnsupportedOperationException();
    }
}
