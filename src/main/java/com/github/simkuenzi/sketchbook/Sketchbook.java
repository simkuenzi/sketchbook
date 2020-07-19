package com.github.simkuenzi.sketchbook;

import java.io.IOException;
import java.util.List;

public interface Sketchbook {
    List<ValidSketch> getSketches() throws IOException;

    ValidSketch sketch(String sketchName);

    ValidSketch newSketch(String baseName);
}
