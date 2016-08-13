package org.matetski.gui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.layout.Pane;
import org.matetski.utils.Model;
import org.matetski.utils.ModelUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;

public class StandardController extends Controller {

    private Canvas canvas;

    private final Model model;

    public Canvas getCanvas() {
        return canvas;
    }

    @Override
    protected HashMap<String, Object> createParameters() {
        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put(ModelUtils.SIZE_PARAMETER, new Dimension((int) canvas.getWidth(),
                (int) canvas.getHeight()));
        return parameters;
    }

    @Override
    public void setMyParameters(HashMap<String, Object> parameters) {
    }

    @Override
    protected void makeInactive() {

    }

    @Override
    protected void makeActive() {

    }

    @Override
    public void stateChanged(HashMap<String, Object> parameters, boolean repaint) {
        parameters.put(ModelUtils.SIZE_PARAMETER, new Dimension((int) canvas.getWidth(),
                (int) canvas.getHeight()));
        model.setParameters(parameters);
        if (repaint) {
            Platform.runLater(() -> model.paint(canvas.getGraphicsContext2D()));
        }
    }

    private final Timer timer;

    public StandardController(Model model) {
        this.model = model;
        timer = new Timer(0, new TimerListener(model));
    }

    public void createCanvas() {
        canvas = new Canvas(getDrawingPane().getWidth(), getDrawingPane().getHeight());
        getDrawingPane().getChildren().add(canvas);
    }

    @FXML
    private Button runPauseButton;

    @FXML
    private Button resetButton;

    @FXML
    private Slider delaySlider;

    @FXML
    private Pane drawingPane;

    @FXML
    private Pane controlPanel;

    private void runPauseButtonAction() {
        if (runPauseButton.getText().equals(RunPauseButtonState.RUN.toString())) {
            makeSubcontrollersInactive();
            timer.setDelay(getDelay());
            timer.start();
            runPauseButton.setText(RunPauseButtonState.PAUSE.toString());
        } else {
            stopSimulation();
        }
    }

    /**
     * Stops execution of the algorithm.
     */
    private void stopSimulation() {
        timer.stop();
        runPauseButton.setText(RunPauseButtonState.RUN.toString());
    }

    private void resetButtonAction() {
        stopSimulation();
        model.setParameters(getParameters());
        Platform.runLater(() -> model.paint(canvas.getGraphicsContext2D()));
        makeSubcontrollersActive();
    }

    @FXML
    private void delayChangedAction() {
        timer.setDelay(getDelay());
    }

    public Pane getControlPanel() {
        return controlPanel;
    }

    private Pane getDrawingPane() {
        return drawingPane;
    }

    private int getDelay() {
        return (int) delaySlider.getValue();
    }

    private class TimerListener implements ActionListener {
        private final Model model;

        TimerListener(Model model) {
            this.model = model;
        }

        @Override
        public void actionPerformed(java.awt.event.ActionEvent e) {
            if (model.canStop()) {
                Platform.runLater(StandardController.this::stopSimulation);
                makeSubcontrollersActive();
                Platform.runLater(() -> {
                    model.setParameters(getParameters());
                    model.paint(canvas.getGraphicsContext2D());
                });
            } else {
                Platform.runLater(() -> {
                    model.iterate();
                    model.paint(canvas.getGraphicsContext2D());
                });
            }
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        runPauseButton.setOnAction((value) -> runPauseButtonAction());
        resetButton.setOnAction((value) -> resetButtonAction());
        delaySlider.valueProperty().addListener((listener) -> delayChangedAction());
    }
}
