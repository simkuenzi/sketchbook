package com.github.simkuenzi.sketchbook;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;

class TestFs {

    void use(Test test) throws Exception {
        Path testPath = Path.of(System.getProperty("com.github.simkuenzi.sketchbook.testfs"));
        if (Files.exists(testPath)) {
            Files.walk(testPath).sorted(Comparator.reverseOrder()).forEach(f -> {
                try {
                    Files.delete(f);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }

        test.run(testPath);
    }

    interface Test {
        void run(Path testFs) throws Exception;
    }
}
