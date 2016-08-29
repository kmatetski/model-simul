package org.matetski.models.tasep;

import javafx.application.Application;
import org.matetski.apps.ModelApplication;
import org.matetski.utils.Model;

/**
 * The application displaying a TASEP evolution.
 *
 * @author K.Matetski
 */

public class TASEPApplication extends ModelApplication {

    private final Model MODEL = new TASEPModel();

    @Override
    protected Model getModel() {
        return MODEL;
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}
