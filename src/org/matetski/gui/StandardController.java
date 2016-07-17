package org.matetski.gui;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.layout.Pane;
import org.matetski.utils.Model;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.util.HashMap;

public class StandardController extends Controller {

    private Canvas canvas;

    @Override
    protected HashMap<String, Object> createParameters() {
        return null;
    }

    public HashMap<String,Object> getParameters() {
        return new HashMap<String, Object>();
    }

    @Override
    public void setParameters(HashMap<String, Object> parameters) {

    }

    private final Timer timer;

    public StandardController(Model model) {
        super(model);
        timer = new Timer(0, new TimerListener(model));
    }

    public void createCanvas() {
        canvas = new Canvas(getDrawingPane().getWidth(), getDrawingPane().getHeight());
        getDrawingPane().getChildren().add(canvas);
    }

    @FXML
    private Button startStopButton;

    @FXML
    private Button resetButton;

    @FXML
    private Slider delaySlider;

    @FXML
    private Pane drawingPane;

    @FXML
    private Pane controlPanel;

    @FXML
    private void startStopButtonAction() {
        if (startStopButton.getText().equals(StartStopButtonState.START.toString())) {
            timer.setDelay(getDelay());
            timer.start();
            startStopButton.setText(StartStopButtonState.STOP.toString());
        } else {
            timer.stop();
            startStopButton.setText(StartStopButtonState.START.toString());
        }
    }

    @FXML
    private void resetButtonAction() {
        setParameters(getDefaultParameters());
    }

    @FXML
    private void delayChangedAction() {
        timer.setDelay(getDelay());
    }

    public Pane getControlPanel() {
        return controlPanel;
    }

    public Pane getDrawingPane() {
        return drawingPane;
    }

    private int getDelay() {
        return (int) delaySlider.getValue();
    }

    private class TimerListener implements ActionListener {
        private final Model model;

        public TimerListener(Model model) {
            this.model = model;
        }

        @Override
        public void actionPerformed(java.awt.event.ActionEvent e) {
            SwingUtilities.invokeLater(() -> {
                model.iterate();
                model.paint(canvas);
            });
        }
    }
}
