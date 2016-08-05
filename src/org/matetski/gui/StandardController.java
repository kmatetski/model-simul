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
    public Canvas getCanvas() {
        return canvas;
    }

    private Canvas canvas;

    @Override
    protected HashMap<String, Object> createParameters() {
        return null;
    }

    public HashMap<String,Object> getParameters() {
        return new HashMap<String, Object>();
    }

    @Override
    public void setMyParameters(HashMap<String, Object> parameters) {

    }

    private final Timer timer;

    public StandardController(Model model) {
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

    @FXML
    private void runPauseButtonAction() {
        if (runPauseButton.getText().equals(RunPauseButtonState.RUN.toString())) {
            timer.setDelay(getDelay());
            timer.start();
            runPauseButton.setText(RunPauseButtonState.PAUSE.toString());
        } else {
            timer.stop();
            runPauseButton.setText(RunPauseButtonState.RUN.toString());
        }
    }

    @FXML
    private void resetButtonAction() {
        //setParameters(getDefaultParameters());
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
                model.paint(canvas.getGraphicsContext2D());
            });
        }
    }
}
