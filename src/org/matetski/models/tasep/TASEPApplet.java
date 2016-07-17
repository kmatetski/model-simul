package org.matetski.models.tasep;

import org.matetski.applets.ModelApplet;
import org.matetski.utils.Model;

public class TASEPApplet extends ModelApplet {

	private final Model MODEL = new TASEPModel();

	@Override
	protected Model getModel() {
		return MODEL;
	}
}