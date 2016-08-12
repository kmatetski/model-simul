package org.matetski.utils;

import javafx.scene.canvas.GraphicsContext;

import java.util.HashMap;

/**
 * An abstract class which provides a skeleton of model algorithms.
 *
 * @author K.Matetski
 */
public abstract class Model {

    private final static String GUI_FILE_NAME = "../../gui/simulator.fxml";

    /**
     * Initializes the model. Should be called before starting execution.
     */
    protected abstract void initialize();

    /**
     * Updates parameters of the model.
     */
    protected abstract void update();

    /**
     * Checks whether the algorithm can stop.
     *
     * @return {@code true} if the algorithm can stop.
     */
    public abstract boolean canStop();

    /**
     * Paints the output of the algorithm.
     *
     * @param graphicsContext The canvas on which the output should be drawn.
     */
    public abstract void paint(GraphicsContext graphicsContext);

    /**
     * Performs an iteration of the algorithm, if the latter is not stopped.
     */
    public void iterate() {
        update();
    }

    public String getGUIFileName() {
        return GUI_FILE_NAME;
    }

    /**
     * Returns the .fxml file describing the control panel.
     *
     * @return the .fxml file name.
     */
    public abstract String getControlGUIFileName();

    /**
     * Returns the model name how it will be written in the header of the window.
     *
     * @return the model name.
     */
    public abstract String getModelName();

    /**
     * Returns a list of the current values of model's parameters.
     *
     * @return a list of model's parameters.
     */
    public abstract HashMap<String, Object> getParameters();

    public abstract void setParameters(HashMap<String, Object> parameters);

    public abstract HashMap<String, Object> getDefaultParameters();
}
