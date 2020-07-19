package com.github.simkuenzi.sketchbook;

interface Action<T extends Sketch> {
    void apply(T sketch) throws Exception;
}
