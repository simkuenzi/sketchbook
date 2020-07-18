package com.github.simkuenzi.sketchbook;

import java.util.Collections;
import java.util.List;

public class MissingSketchbook implements Sketchbook {
    @Override
    public List<Sketch> getSketches() {
        return Collections.emptyList();
    }

    @Override
    public Sketch sketch(String sketchName) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Sketch newSketch(String baseName) {
        throw new UnsupportedOperationException();
    }
}
