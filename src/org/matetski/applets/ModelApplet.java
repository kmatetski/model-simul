package org.matetski.applets;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import org.matetski.gui.Controller;
import org.matetski.gui.StandardController;
import org.matetski.utils.Model;
import org.matetski.utils.ModelSimulator;
import org.matetski.utils.ModelUtils;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.HashMap;

/**
 * @author K.Matetski
 */

public abstract class ModelApplet extends JApplet {

    protected abstract Model getModel();

    @Override
    public final void init() {
        //Frame frame = (Frame) getParent().getParent();
        //frame.setTitle(getModel().getModelName());
        SwingUtilities.invokeLater(
                this::initSwing
        );
    }

    private void initSwing() {
        JFXPanel fxPanel = new JFXPanel();
        add(fxPanel);
        Platform.runLater(
                () -> initApplet(getModel(), fxPanel)
        );
    }

    private void initApplet(Model model, JFXPanel fxPanel) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(model.getGUIFileName()));
            StandardController controller = new StandardController(model);
            loader.setController(controller);
            Rectangle visualBounds = getContentPane().getBounds();
            fxPanel.setScene(new Scene(loader.load(), visualBounds.getWidth(), visualBounds.getHeight()));
            controller.createCanvas();
            loadSubcontrollers(model, controller);

            HashMap<String, Object> parameters = createParameters(model, controller);
            model.setParameters(parameters);
            controller.setParameters(parameters);
            model.paint(controller.getCanvas().getGraphicsContext2D());
        } catch (IOException e) {
            e.printStackTrace();
        }
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
     * The main applet method.
     */
    @Override
    public void start() {
        ModelSimulator simulator = new ModelSimulator(getModel());
        simulator.start();
    }
}
