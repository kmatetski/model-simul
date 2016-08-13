package org.matetski.models.tasep;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Slider;
import org.matetski.gui.Controller;
import java.net.URL;
import java.util.*;

public class TASEPController extends Controller implements Initializable {

    @FXML
    private ChoiceBox<InitialData> initialData;

    @FXML
    private ChoiceBox<Angle> angle;

    @FXML
    private Slider particleRadius;

    @FXML
    private Slider jumpRate;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initialData.getItems().clear();
        initialData.setItems(FXCollections.observableArrayList(InitialData.values()));
        angle.getItems().clear();
        angle.setItems(FXCollections.observableArrayList(Angle.values()));
    }

    @Override
    protected HashMap<String, Object> createParameters() {
        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put(TASEPUtils.INITIAL_DATA_PARAMETER, initialData.getValue());
        parameters.put(TASEPUtils.ANGLE_PARAMETER, angle.getValue());
        parameters.put(TASEPUtils.PARTICLE_SIZE_PARAMETER, particleRadius.getValue());
        parameters.put(TASEPUtils.JUMP_RATE_PARAMETER, jumpRate.getValue());
        return parameters;
    }

    @Override
    protected void setMyParameters(HashMap<String, Object> parameters) {
        initialData.setValue((InitialData) parameters.get(TASEPUtils.INITIAL_DATA_PARAMETER));
        angle.setValue((Angle) parameters.get(TASEPUtils.ANGLE_PARAMETER));
        particleRadius.setValue((Double) parameters.get(TASEPUtils.PARTICLE_SIZE_PARAMETER));
        jumpRate.setValue((Double) parameters.get(TASEPUtils.JUMP_RATE_PARAMETER));
    }

    @Override
    protected void makeUnactive() {
        initialData.setDisable(true);
        angle.setDisable(true);
        particleRadius.setDisable(true);
        jumpRate.setDisable(true);
    }

    @Override
    protected void makeActive() {
        initialData.setDisable(false);
        angle.setDisable(false);
        particleRadius.setDisable(false);
        jumpRate.setDisable(false);
    }
}
