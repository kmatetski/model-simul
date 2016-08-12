package org.matetski.models.dummy;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import org.matetski.utils.Model;

import java.util.HashMap;

/**
 * An algorithm simulating a simple model.
 *
 * @author K.Matetski
 */

public class DummyModel extends Model {

    private final static String CONTROL_GUI_FILE_NAME = "./dummy.fxml";
    private final static String MODEL_NAME = "Dummy model";

    private int colorNumber = 0;

    @Override
    protected void initialize() {

    }

    @Override
    public void update() {
        colorNumber = (colorNumber + 1) % 3;
    }

    @Override
    public boolean canStop() {
        return false;
    }

    @Override
    public void paint(GraphicsContext graphicsContext) {
        switch (colorNumber) {
            case 0:
                graphicsContext.setFill(Color.GREEN);
                break;
            case 1:
                graphicsContext.setFill(Color.RED);
                break;
            case 2:
                graphicsContext.setFill(Color.BLUE);
        }
        graphicsContext.fillRect(0, 0, graphicsContext.getCanvas().getWidth(), graphicsContext.getCanvas().getHeight());
    }

    @Override
    public String getControlGUIFileName() {
        return CONTROL_GUI_FILE_NAME;
    }

    @Override
    public String getModelName() {
        return MODEL_NAME;
    }

    @Override
    public HashMap<String, Object> getParameters() {
        return null;
    }

    @Override
    public void setParameters(HashMap<String, Object> parameters) {

    }

    @Override
    public HashMap<String, Object> getDefaultParameters() {
        return null;
    }
}