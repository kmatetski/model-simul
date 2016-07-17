package org.matetski.applets;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import org.matetski.gui.Controller;
import org.matetski.gui.StandardController;
import org.matetski.utils.Model;
import org.matetski.utils.ModelSimulator;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.HashMap;

/**
 * Created by k.matetski on 7/14/16.
 */

public abstract class ModelApplet extends JApplet {

    protected abstract Model getModel();

    @Override
    public final void init() {
        Frame frame = (Frame)getParent().getParent();
        frame.setTitle(getModel().getModelName());
        SwingUtilities.invokeLater(
                        () -> initSwing()
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

            FXMLLoader controlLoader = new FXMLLoader(getClass().getResource(model.getControlGUIFileName()));
            controller.getControlPanel().getChildren().add(controlLoader.load());

            //controller.addSubcontroller(controlLoader.getController());
        } catch (IOException e) {
            e.printStackTrace();
        }
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
