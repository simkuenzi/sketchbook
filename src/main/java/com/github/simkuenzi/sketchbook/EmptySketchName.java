package com.github.simkuenzi.sketchbook;

public class EmptySketchName implements SketchName {
    @Override
    public String getValue() {
        return "";
    }

    @Override
    public NameValidity getValidity() {
        return NameValidity.VALID;
    }
}
