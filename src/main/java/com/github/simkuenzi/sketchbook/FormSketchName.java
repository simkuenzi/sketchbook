package com.github.simkuenzi.sketchbook;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class FormSketchName implements SketchName {

    private static final String SKETCH_NAME_REGEX = "[^<>|\\\\:()&]+";
    private static final Pattern SKETCH_NAME_PATTERN = Pattern.compile(SKETCH_NAME_REGEX);
    private static final String FORM_PARAM_NAME = "name";

    private final Sketchbook sketchbook;
    private final Map<String, List<String>> form;

    public FormSketchName(Sketchbook sketchbook, Map<String, List<String>> form) {
        this.sketchbook = sketchbook;
        this.form = form;
    }

    @Override
    public String getValue() {
        return form.containsKey(FORM_PARAM_NAME) ? form.get(FORM_PARAM_NAME).get(0) : "";
    }

    @Override
    public NameValidity getValidity() {
        if (form.containsKey(FORM_PARAM_NAME) && SKETCH_NAME_PATTERN.matcher(getValue()).matches()) {
            return NameValidity.VALID;
        } else {
            return NameValidity.MALFORMED;
        }
    }

    @Override
    public String getPattern() {
        return SKETCH_NAME_REGEX;
    }
}
