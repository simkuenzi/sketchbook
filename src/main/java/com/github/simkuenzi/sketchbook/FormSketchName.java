package com.github.simkuenzi.sketchbook;

import io.javalin.http.Context;

import java.util.regex.Pattern;

public class FormSketchName implements SketchName {

    private static final String SKETCH_NAME_REGEX = "[^<>|\\\\:()&]+";
    private static final Pattern SKETCH_NAME_PATTERN = Pattern.compile(SKETCH_NAME_REGEX);
    private static final String FORM_PARAM_NAME = "name";

    private final Context ctx;

    public FormSketchName(Context ctx) {
        this.ctx = ctx;
    }

    @Override
    public String getName() {
        return ctx.formParamMap().containsKey(FORM_PARAM_NAME) ? ctx.formParam(FORM_PARAM_NAME) : "";
    }

    @Override
    public boolean isValid() {
        return ctx.formParamMap().containsKey(FORM_PARAM_NAME) && SKETCH_NAME_PATTERN.matcher(getName()).matches();
    }

    @Override
    public String getPattern() {
        return SKETCH_NAME_REGEX;
    }
}
