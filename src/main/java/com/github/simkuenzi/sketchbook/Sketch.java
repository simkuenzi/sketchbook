package com.github.simkuenzi.sketchbook;

import java.io.IOException;

public interface Sketch {
    SketchName getName();
    String getContent() throws IOException;

}
