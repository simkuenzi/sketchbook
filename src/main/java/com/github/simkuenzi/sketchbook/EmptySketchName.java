package com.github.simkuenzi.sketchbook;

public class EmptySketchName implements SketchName {
    @Override
    public String getName() {
        return "";
    }

    @Override
    public boolean isValid() {
        return true;
    }
}
