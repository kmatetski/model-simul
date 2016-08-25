package org.matetski.models.tasep;

/**
 * Contains constants describing the model.
 *
 * @author K.Matetski
 */
final class TASEPUtils {
    /**
     * The names of parameters used by the model.
     */
    public static final String JUMP_RATE_PARAMETER = "JUMP_RATE";
    public static final String PARTICLE_SIZE_PARAMETER = "PARTICLE_SIZE";
    public static final String ANGLE_PARAMETER = "ANGLE";
    public static final String INITIAL_DATA_PARAMETER = "INITIAL_DATA";

    /**
     * The GUI file of the control panel.
     */
    public final static String CONTROL_GUI_FILE_NAME = "./tasep.fxml";

    /**
     * The name displayed at the header of the window.
     */
    public final static String MODEL_NAME = "TASEP";

    /**
     * The default value of the jump rate of particles.
     */
    public final static double DEFAULT_JUMP_RATE = 0.5;

    /**
     * The default value of a particle's diameter on the canvas.
     */
    public final static double DEFAULT_PARTICLE_SIZE = 2;

    /**
     * The default value of the angle how the interface should be drawn, e.g. flat or titled.
     */
    public final static Angle DEFAULT_ANGLE = Angle.ZERO;

    /**
     * The default value of the initial data, e.g. flat, step or half flat.
     */
    public final static InitialData DEFAULT_INITIAL_DATA = InitialData.FLAT;

    /**
     * The margin in pixels from the bottom on the canvas where the particles are drawn.
     */
    public final static int BOTTOM_MARGIN_TASEP = 10;

    /**
     * The margin in pixels from the bottom on the canvas where the interface is drawn.
     */
    public final static int BOTTOM_MARGIN_HEIGHTS = BOTTOM_MARGIN_TASEP * 2;

    /**
     * The line width of the axis on the canvas.
     */
    public final static double AXIS_LINE_WIDTH = 0.5;

    /**
     * The line width of the interface.
     */
    public final static double STANDARD_LINE_WIDTH = 1;
}
