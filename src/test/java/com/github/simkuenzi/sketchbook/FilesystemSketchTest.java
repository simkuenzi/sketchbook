package com.github.simkuenzi.sketchbook;

import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.Assert.*;

public class FilesystemSketchTest {
    @Test
    public void rename() throws Exception {
        new TestFs().use(testFs -> {
            Path sketchFile = testFs.resolve("origName");
            FilesystemSketch sketch = new FilesystemSketch(sketchFile);
            sketch.save("origContent");

            sketch.save("newName", "newContent",
                    saved -> {
                        assertFalse(Files.exists(sketchFile));
                        assertTrue(Files.exists(testFs.resolve("newName")));
                        assertEquals("newContent", saved.getContent());
                    },
                    duplicate -> fail()
            );
        });
    }

    @Test
    public void renameToExisting() throws Exception {
        new TestFs().use(testFs -> {
            Path sketchFile = testFs.resolve("origName");
            FilesystemSketch sketch = new FilesystemSketch(sketchFile);
            sketch.save("origContent");
            new FilesystemSketch(testFs.resolve("newName")).save("otherContent");
            sketch.save("newName", "newContent",
                    saved -> fail(),
                    duplicate -> assertEquals(sketch, duplicate));
        });
    }

    @Test
    public void save() throws Exception {
        new TestFs().use(testFs -> {
            Path sketchFile = testFs.resolve("name");
            FilesystemSketch sketch = new FilesystemSketch(sketchFile);
            sketch.save("origContent");

            sketch.save("name", "newContent",
                    saved -> {
                        assertTrue(Files.exists(sketchFile));
                        assertEquals("newContent", saved.getContent());
                    },
                    duplicate -> fail()
            );
        });
    }
}