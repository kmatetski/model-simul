package org.matetski.models.tasep;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Slider;
import javafx.scene.layout.Pane;
import org.matetski.gui.Controller;
import org.matetski.utils.Model;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.*;

public class TASEPController extends Controller implements Initializable {

    @FXML
    private ChoiceBox<InitialData> initialData;

    @FXML
    private ChoiceBox<Angle> angle;

    @FXML
    private Slider particleRadius;

    public TASEPController(Model model) {
        super(model);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initialData.getItems().clear();
        initialData.setItems(FXCollections.observableArrayList(InitialData.values()));
        initialData.setValue(InitialData.getDefaultValue());

        angle.getItems().clear();
        angle.setItems(FXCollections.observableArrayList(Angle.values()));
        angle.setValue(Angle.getDefaultValue());
    }

    @Override
    protected HashMap<String, Object> createParameters() {
        return null;
    }

    @Override
    public HashMap<String, Object> getParameters() {
        return null;
    }

    @Override
    public void setParameters(HashMap<String, Object> parameters) {

    }
}
