package com.github.simkuenzi.sketchbook;

import com.github.simkuenzi.webplay.play.RecordedTest;
import org.junit.Assert;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class WebPlayTest {

    private static final int port = 9111;

    @Test
    public void play() throws Exception {
        new TestFs().use(testFs -> {
            Path pathFile = testFs.resolve("pathfile");
            Path sketchesDir = testFs.resolve("sketches");
            Path userDir = sketchesDir.resolve("anon");
            Files.writeString(pathFile, sketchesDir.toString(), StandardOpenOption.CREATE);
            Files.createDirectories(userDir);
            Files.writeString(userDir.resolve("change"), "Hello\nWorld");
            Files.writeString(userDir.resolve("rename"), "This file will be renamed.");
            Files.writeString(userDir.resolve("rename_and_change"), "And this will be renamed to.\nAnd even changed!");

            Server server = new Server(port, "/sketchbook", pathFile);
            server.start();
            try {
                RecordedTest recordedTest = new RecordedTest(WebPlayTest.class.getResource("test.xml"));
                recordedTest.play("http://localhost:" + port, Assert::assertEquals);
            } finally {
                server.stop();
            }
        });

    }
}
