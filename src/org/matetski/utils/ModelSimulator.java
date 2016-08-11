package org.matetski.utils;

/**
 * A simulator class, which runs a model algorithm in a new thread.
 *
 * @author K.Matetski
 */
public class ModelSimulator extends Thread {

    /**
     * A model algorithm.
     */
    private final Model model;

    /**
     * Creates an instance with a given model and a pane, and a standard GUI
     * builder.
     *
     * @param model A model algorithm.
     */
    public ModelSimulator(Model model) {
        this.model = model;
    }

    /**
     * The main method of the thread.
     */
    @Override
    public void run() {
        model.initialize();
    }
}