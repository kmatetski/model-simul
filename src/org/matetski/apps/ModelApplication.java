package org.matetski.apps;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.paint.*;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.matetski.gui.Controller;
import org.matetski.gui.StandardController;
import org.matetski.utils.Model;
import org.matetski.utils.ModelUtils;

import java.awt.*;
import java.io.IOException;
import java.util.HashMap;

/**
 * @author K.Matetski
 */
public abstract class ModelApplication extends Application {

    protected abstract Model getModel();

    private void init(Stage primaryStage) throws Exception {
        primaryStage.setTitle(getModel().getModelName());
        FXMLLoader loader = new FXMLLoader(getClass().getResource(getModel().getGUIFileName()));
        StandardController controller = new StandardController(getModel());
        loader.setController(controller);
        Rectangle2D visualBounds = Screen.getPrimary().getVisualBounds();
        primaryStage.setScene(new Scene(loader.load(), visualBounds.getWidth(), visualBounds.getHeight()));
        primaryStage.show();

        controller.createCanvas();
        loadSubcontrollers(getModel(), controller);

        HashMap<String, Object> parameters = createParameters(getModel(), controller);
        getModel().setParameters(parameters);
        controller.setParameters(parameters);
        getModel().paint(controller.getCanvas().getGraphicsContext2D());
    }

    private void loadSubcontrollers(Model model, StandardController controller) throws IOException {
        FXMLLoader controlLoader = new FXMLLoader(getClass().getResource(model.getControlGUIFileName()));
        controller.getControlPanel().getChildren().add(controlLoader.load());
        Controller subController = controlLoader.getController();
        subController.setParentController(controller);
        controller.addSubcontroller(subController);
    }

    private HashMap<String, Object> createParameters(Model model, StandardController controller) {
        HashMap<String, Object> parameters = model.getDefaultParameters();
        parameters.put(ModelUtils.SIZE_PARAMETER, new Dimension((int) controller.getCanvas().getWidth(),
                (int) controller.getCanvas().getHeight()));
        return parameters;
    }

    /**
     * The main application method.
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        getModel().initialize();
        init(primaryStage);
    }
}
