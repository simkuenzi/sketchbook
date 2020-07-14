package com.github.simkuenzi.sketchbook;

public interface SketchName {
    String getName();

    default String getPattern() {
        return "";
    }

    boolean isValid();
}
