package com.github.simkuenzi.sketchbook;

import java.io.IOException;
import java.util.List;

public interface Sketchbook {
    List<ValidSketch> getSketches() throws IOException;

    ValidSketch sketch(SketchId id);

    ValidSketch newSketch(String baseName);
}
