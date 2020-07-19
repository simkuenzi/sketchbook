package com.github.simkuenzi.sketchbook;

import java.io.IOException;

public interface SketchName {
    String getName();

    default String getPattern() {
        return "";
    }

    NameValidity getValidity() throws IOException;
}
