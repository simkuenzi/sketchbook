package com.github.simkuenzi.sketchbook;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.IOException;

public class IncomingSketch {
    private final String baseName;
    private final String content;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public IncomingSketch(@JsonProperty("baseName") String baseName, @JsonProperty("content") String content) {
        this.baseName = baseName;
        this.content = content;
    }

    void save(Sketchbook sketchbook) throws IOException {
        sketchbook.newSketch(baseName).save(content);
    }
}
