package org.matetski.utils;

import javafx.scene.canvas.Canvas;

import java.awt.*;
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
	 * Paints the output of the algorithm.
	 *
	 * @param canvas
	 *            The canvas on which the output should be drawn.
	 */
	public abstract void paint(Canvas canvas);

	/**
	 * Performs an iteration of the algorithm, if the latter is not stopped.
	 */
	public void iterate() {
		update();
	}

    public String getGUIFileName() {
        return GUI_FILE_NAME;
    }

    public abstract String getControlGUIFileName();

    public abstract String getModelName();

    public abstract HashMap<String, Object> getParameters();

    public abstract void setParameters(HashMap<String, Object> parameters);

    public abstract HashMap<String, Object> getDefaultParameters();
}
