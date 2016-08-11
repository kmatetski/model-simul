package org.matetski.models.tasep;

import org.matetski.applets.ModelApplet;
import org.matetski.utils.Model;

/**
 * The applet displaying a TASEP evolution.
 */
public class TASEPApplet extends ModelApplet {
	private final Model MODEL = new TASEPModel();

	@Override
	protected Model getModel() {
		return MODEL;
	}
}