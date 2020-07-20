package com.github.simkuenzi.sketchbook;

import org.junit.Test;

import java.util.Collections;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class FormSketchNameTest {

    @Test
    public void duplicate() throws Exception {
        new TestFs().use(testFs -> {
            Sketchbook sketchbook = new FilesystemSketchbook(testFs);
            sketchbook.sketch(SketchId.forName("existing")).save("content");

            SketchName sketchName = new FormSketchName(sketchbook, Map.of("name", Collections.singletonList("existing")));
            assertEquals(NameValidity.DUPLICATE_NAME, sketchName.getValidity());
        });
    }

    @Test
    public void valid() throws Exception {
        new TestFs().use(testFs -> {
            Sketchbook sketchbook = new FilesystemSketchbook(testFs);
            SketchName sketchName = new FormSketchName(sketchbook, Map.of("name", Collections.singletonList("valid")));
            assertEquals(NameValidity.VALID, sketchName.getValidity());
        });
    }

}