package com.github.simkuenzi.sketchbook;

import java.io.IOException;
import java.util.List;

public interface Sketchbook {
    List<Sketch> getSketches() throws IOException;

    Sketch sketch(String sketchName);

    Sketch newSketch(String baseName);
}
