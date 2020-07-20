package com.github.simkuenzi.sketchbook;

import java.io.IOException;

public interface Sketch {
    SketchId getId();
    SketchName getName();
    String getContent() throws IOException;

}
