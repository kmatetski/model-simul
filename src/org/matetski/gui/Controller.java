package org.matetski.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author K.Matetski
 */
public abstract class Controller {

    /**
     * A list of subcontrollers of the controller, e.g. controllers for a particular model.
     */
    private List<Controller> subcontrollers;

    /**
     * Returns current values of parameters of the given controller.
     */
    protected abstract HashMap<String, Object> createParameters();

    /**
     * Returns a collection of parameters obtained from all subcontrollers.
     */
    public HashMap<String, Object> getParameters() {
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

    protected void makeSubcontrollersUnactive() {
        makeUnactive();
        if (subcontrollers != null) {
            for (Controller controller : subcontrollers) {
                controller.makeUnactive();
            }
        }
    }

    protected void makeSubcontrollersActive() {
        makeActive();
        if (subcontrollers != null) {
            for (Controller controller : subcontrollers) {
                controller.makeActive();
            }
        }
    }

    protected abstract void makeUnactive();

    protected abstract void makeActive();
}
