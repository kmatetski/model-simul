package org.matetski.gui;

import javafx.fxml.Initializable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author K.Matetski
 */
public abstract class Controller implements Initializable {

    /**
     * A list of subcontrollers of the controller, e.g. controllers for a particular model.
     */
    private List<Controller> subcontrollers;

    private Controller parentController;

    /**
     * Returns current values of parameters of the given controller.
     */
    protected abstract HashMap<String, Object> createParameters();

    /**
     * Returns a collection of parameters obtained from all subcontrollers.
     */
    HashMap<String, Object> getParameters() {
        HashMap<String, Object> parameters = new HashMap<>();
        parameters.putAll(createParameters());
        if (subcontrollers != null) {
            for (Controller controller : subcontrollers) {
                parameters.putAll(controller.createParameters());
            }
        }
        return parameters;
    }

    protected abstract void setMyParameters(HashMap<String, Object> parameters);

    public final void setParameters(HashMap<String, Object> parameters) {
        setMyParameters(parameters);
        if (subcontrollers != null) {
            for (Controller controller : subcontrollers) {
                controller.setMyParameters(parameters);
            }
        }
    }

    public void addSubcontroller(Controller controller) {
        if (subcontrollers == null) {
            subcontrollers = new ArrayList<>();
        }
        subcontrollers.add(controller);
    }

    void makeSubcontrollersInactive() {
        makeInactive();
        if (subcontrollers != null) {
            subcontrollers.forEach(Controller::makeInactive);
        }
    }

    void makeSubcontrollersActive() {
        makeActive();
        if (subcontrollers != null) {
            subcontrollers.forEach(Controller::makeActive);
        }
    }

    public void setParentController(Controller controller) {
        parentController = controller;
    }

    protected Controller getParentController() {
        return parentController;
    }

    protected abstract void makeInactive();

    protected abstract void makeActive();

    public abstract void stateChanged(HashMap<String, Object> parameters, boolean repaint);
}
