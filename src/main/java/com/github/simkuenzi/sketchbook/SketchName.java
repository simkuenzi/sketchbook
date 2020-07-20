package com.github.simkuenzi.sketchbook;

import java.io.IOException;

public interface SketchName {
    String getValue();

    default String getPattern() {
        return "";
    }

    NameValidity getValidity() throws IOException;
}
