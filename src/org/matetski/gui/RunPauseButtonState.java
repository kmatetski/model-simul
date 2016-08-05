package org.matetski.gui;

/*
 * States of the button which stops and resumes execution of the algorithm.
 */
public enum RunPauseButtonState {
    RUN("Run"),
    PAUSE("Pause");

    /**
     * Name of the button state.
     */
    private final String name;

    RunPauseButtonState(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
