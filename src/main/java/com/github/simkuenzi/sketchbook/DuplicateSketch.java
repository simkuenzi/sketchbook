package com.github.simkuenzi.sketchbook;

import java.io.IOException;

public class DuplicateSketch implements Sketch {
    private final Sketch sketch;

    public DuplicateSketch(Sketch sketch) {
        this.sketch = sketch;
    }

    @Override
    public SketchName getName() {
        return new DuplicateSketchName(sketch.getName());
    }

    @Override
    public String getContent() throws IOException {
        return sketch.getContent();
    }
}
