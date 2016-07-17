package org.matetski.gui;

import org.matetski.utils.Model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by k.matetski on 7/17/16.
 */
public abstract class Controller {
    private final Model model;
    private List<Controller> subcontrollers;

    public Controller(Model model) {
        this.model = model;
    }

    public HashMap<String, Object> getDefaultParameters() {
        return model.getDefaultParameters();
    }

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

    public abstract void setParameters(HashMap<String, Object> parameters);

    public void updateParameters(HashMap<String, Object> parameters) {
        model.setParameters(parameters);
    }

    public void addSubcontroller(Controller controller) {
        if (subcontrollers == null) {
            subcontrollers = new ArrayList<>();
        }
        subcontrollers.add(controller);
    }
}
