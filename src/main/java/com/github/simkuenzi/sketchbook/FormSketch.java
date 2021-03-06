package com.github.simkuenzi.sketchbook;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class FormSketch implements Sketch {

    private final Sketchbook sketchbook;
    private final SketchId id;
    private final Map<String, List<String>> form;

    public FormSketch(Sketchbook sketchbook, SketchId id, Map<String, List<String>> form) {
        this.sketchbook = sketchbook;
        this.id = id;
        this.form = form;
    }

    @Override
    public SketchId getId() {
        return id;
    }

    @Override
    public SketchName getName() {
        return new FormSketchName(sketchbook, form);
    }

    @Override
    public String getContent() {
        return form.getOrDefault("content", Collections.singletonList("")).get(0);
    }

    public void save(Action<Sketch> onStay, Action<ValidSketch> onMove) throws Exception {
        if (getName().getValidity() == NameValidity.VALID) {
            ValidSketch sketch = sketchbook.sketch(id);
            sketch.save(getName().getValue(), getContent(),
                    newSketch -> {
                        if (newSketch != sketch) {
                            onMove.apply(newSketch);
                        } else {
                            onStay.apply(newSketch);
                        }
                    },
                    duplicate -> onStay.apply(new DuplicateSketch(this))
            );
        } else {
            onStay.apply(this);
        }
    }
 }
