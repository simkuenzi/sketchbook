package com.github.simkuenzi.sketchbook;

import java.nio.file.Path;
import java.util.regex.Pattern;

public class SketchId {
    private static final Pattern ESCAPED_PATTERN = Pattern.compile("(__)|(_\\+)");
    private static final Pattern UNESCAPED_PATTERN = Pattern.compile("(_)|(\\h)");

    private final String id;

    public SketchId(String id) {
        this.id = id;
    }

    String asName() {
        return ESCAPED_PATTERN.matcher(id).replaceAll(match -> match.group(1) != null ? "_" : " ");
    }

    Path asPath() {
        return Path.of(id);
    }

    @Override
    public String toString() {
        return id;
    }

    static SketchId forPath(Path path) {
        return new SketchId(path.getFileName().toString());
    }

    static SketchId forName(String name) {
        return new SketchId(UNESCAPED_PATTERN.matcher(name).replaceAll(match -> match.group(1) != null ? "__" : "_+"));
    }
}
