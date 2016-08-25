package org.matetski.models.tasep;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Slider;
import org.matetski.gui.Controller;

import java.net.URL;
import java.util.*;

import static org.matetski.models.tasep.TASEPUtils.*;

public class TASEPController extends Controller {

    @SuppressWarnings("CanBeFinal")
    @FXML
    private ChoiceBox<InitialData> initialData;

    @SuppressWarnings("CanBeFinal")
    @FXML
    private ChoiceBox<Angle> angle;

    @FXML
    private Slider particleRadius;

    @SuppressWarnings("CanBeFinal")
    @FXML
    private Slider jumpRate;

    /**
     * Updates parameters after their change on the panel.
     *
     * @param repaint if {@code true} if the model should be repainted after changing its parameters.
     */
    private void stateChangedAction(boolean repaint) {
        HashMap<String, Object> parameters = createParameters();
        if (getParentController() != null) {
            getParentController().stateChanged(parameters, repaint);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initialData.getItems().clear();
        initialData.setItems(FXCollections.observableArrayList(InitialData.values()));
        angle.getItems().clear();
        angle.setItems(FXCollections.observableArrayList(Angle.values()));

        initialData.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> stateChangedAction(true)
        );
        angle.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> stateChangedAction(true)
        );
        particleRadius.valueProperty().addListener((listener) -> stateChangedAction(true));
        jumpRate.valueProperty().addListener((listener) -> stateChangedAction(false));
    }

    @Override
    protected HashMap<String, Object> createParameters() {
        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put(INITIAL_DATA_PARAMETER, initialData.getValue());
        parameters.put(ANGLE_PARAMETER, angle.getValue());
        parameters.put(PARTICLE_SIZE_PARAMETER, particleRadius.getValue());
        parameters.put(JUMP_RATE_PARAMETER, jumpRate.getValue());
        return parameters;
    }

    @Override
    protected void setMyParameters(HashMap<String, Object> parameters) {
        initialData.setValue((InitialData) parameters.get(INITIAL_DATA_PARAMETER));
        angle.setValue((Angle) parameters.get(ANGLE_PARAMETER));
        particleRadius.setValue((Double) parameters.get(PARTICLE_SIZE_PARAMETER));
        jumpRate.setValue((Double) parameters.get(JUMP_RATE_PARAMETER));
    }

    @Override
    protected void makeInactive() {
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

    @Override
    public void stateChanged(HashMap<String, Object> parameters, boolean repaint) {
    }
}
