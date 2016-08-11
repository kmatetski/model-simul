package org.matetski.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author K.Matetski
 */
public abstract class Controller {

    private List<Controller> subcontrollers;

    protected abstract HashMap<String, Object> createParameters();

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
}
