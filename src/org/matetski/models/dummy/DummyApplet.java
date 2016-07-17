package org.matetski.models.dummy;

import org.matetski.applets.ModelApplet;
import org.matetski.utils.Model;
import org.matetski.utils.ModelSimulator;

/**
 * An applet which runs a dummy model simulation.
 * 
 * @author K.Matetski
 */

public class DummyApplet extends ModelApplet {

    private final Model MODEL = new DummyModel();

    @Override
    protected Model getModel() {
        return MODEL;
    }
}