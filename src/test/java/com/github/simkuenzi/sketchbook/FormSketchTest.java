package com.github.simkuenzi.sketchbook;

import org.junit.Test;

import java.nio.file.Path;
import java.util.Collections;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class FormSketchTest {

    @Test
    public void rename() throws Exception {
        new TestFs().use(testFs -> {
            Path sketchFile = testFs.resolve("origName");
            FilesystemSketch sketch = new FilesystemSketch(sketchFile);
            sketch.save("origContent");

            Sketchbook sketchbook = new FilesystemSketchbook(testFs);
            FormSketch formSketch = new FormSketch(sketchbook, "origName", Map.of(
                    "name", Collections.singletonList("newName"),
                    "content", Collections.singletonList("newContent")));

            formSketch.save(s -> fail(), s -> {
                assertEquals("newName", s.getValidName());
                assertEquals("newContent", s.getContent());
            });
        });
    }

    @Test
    public void renameToExisting() throws Exception {
        new TestFs().use(testFs -> {
            Path sketchFile = testFs.resolve("origName");
            FilesystemSketch sketch = new FilesystemSketch(sketchFile);
            sketch.save("origContent");

            new FilesystemSketch(testFs.resolve("newName")).save("otherContent");

            Sketchbook sketchbook = new FilesystemSketchbook(testFs);
            FormSketch formSketch = new FormSketch(sketchbook, "origName", Map.of(
                    "name", Collections.singletonList("newName"),
                    "content", Collections.singletonList("newContent")));

            formSketch.save(
                    s -> {
                        assertEquals("newName", s.getName().getName());
                        assertEquals(NameValidity.DUPLICATE_NAME, s.getName().getValidity());
                        assertEquals("newContent", s.getContent());
                    },
                    s -> fail());
        });
    }

    @Test
    public void save() throws Exception {
        new TestFs().use(testFs -> {
            Path sketchFile = testFs.resolve("name");
            FilesystemSketch sketch = new FilesystemSketch(sketchFile);
            sketch.save("origContent");

            Sketchbook sketchbook = new FilesystemSketchbook(testFs);
            FormSketch formSketch = new FormSketch(sketchbook, "name", Map.of(
                    "name", Collections.singletonList("name"),
                    "content", Collections.singletonList("newContent")));

            formSketch.save(s -> {
                        assertEquals("name", s.getName().getName());
                        assertEquals("newContent", s.getContent());
                    },
                    s -> fail());
        });
    }
}