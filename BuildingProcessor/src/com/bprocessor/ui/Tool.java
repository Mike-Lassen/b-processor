package com.bprocessor.ui;

public abstract class Tool implements InputListener {
    protected SketchView view;


    public Tool(SketchView view) {
        this.view = view;
    }

    public void prepare() {

    }
    public void finish() {

    }
}
