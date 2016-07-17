package org.matetski.gui;

/*
 * States of the button which stops and resumes execution of the algorithm.
 */
public enum StartStopButtonState {
    START("Start"),
    STOP("Stop");

    /**
     * Name of the button state.
     */
    private final String name;

    StartStopButtonState(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
