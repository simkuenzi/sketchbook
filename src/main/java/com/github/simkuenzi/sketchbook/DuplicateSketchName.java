package com.github.simkuenzi.sketchbook;

public class DuplicateSketchName implements SketchName {
    private final SketchName name;

    public DuplicateSketchName(SketchName name) {
        this.name = name;
    }

    @Override
    public String getValue() {
        return name.getValue();
    }

    @Override
    public NameValidity getValidity() {
        return NameValidity.DUPLICATE_NAME;
    }
}
