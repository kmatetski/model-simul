package org.matetski.models.tasep;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import org.matetski.utils.Model;
import org.matetski.utils.ModelUtils;

import java.awt.*;
import java.util.*;

/**
 * An algorithm simulating the TASEP model.
 *
 * @author K.Matetski
 */

public class TASEPModel extends Model {

    /**
     * The default value of the jump rate of particles.
     */
    private final static double DEFAULT_JUMP_RATE = 0.5;

    /**
     * The default value of a particle's diameter on the canvas.
     */
    private final static double DEFAULT_PARTICLE_SIZE = 2;

    /**
     * The default value of the angle how the interface should be drawn, e.g. flat or titled.
     */
    private final static Angle DEFAULT_ANGLE = Angle.ZERO;

    /**
     * The default value of the initial data, e.g. flat, step or half flat.
     */
    private final static InitialData DEFAULT_INITIAL_DATA = InitialData.FLAT;

    /**
     * The margin in pixels from the bottom on the canvas where the particles are drawn.
     */
    private final static int BOTTOM_MARGIN_TASEP = 10;

    /**
     * The margin in pixels from the bottom on the canvas where the interface is drawn.
     */
    private final static int BOTTOM_MARGIN_HEIGHTS = BOTTOM_MARGIN_TASEP * 2;

    /**
     * The line width of the axis on the canvas.
     */
    private final static double AXIS_LINE_WIDTH = 0.5;

    /**
     * The line width of the interface.
     */
    private final static double STANDARD_LINE_WIDTH = 1;

    /**
     * Jump rate of the TASEP particles.
     */
    private double jumpRate;

    /**
     * This array contains positions of the particles. The 0th element is the right most particle.
     */
    private int[] particles;

    /**
     * Initial configuration of particles, e.g. flat, step or half flat.
     */
    private InitialData initialData;

    /**
     * Angle of how the interface will be drawn, e.g. flat or tilted.
     */
    private Angle angle;

    /**
     * The diameter of a particle, how it will be drawn on the canvas.
     */
    private double particleSize;

    /**
     * The model time, needed to draw the average value.
     */
    private double modelTime = 0;

    /**
     * Is true if the algorithm can be stopped, e.g. if the growth process goes above the window.
     */
    private boolean canBeStopped = false;

    @Override
    public String getControlGUIFileName() {
        return TASEPUtils.CONTROL_GUI_FILE_NAME;
    }

    @Override
    public String getModelName() {
        return TASEPUtils.MODEL_NAME;
    }

    @Override
    public HashMap<String, Object> getParameters() {
        return null;
    }

    @Override
    public void setParameters(HashMap<String, Object> parameters) {
        if (parameters != null) {
            initialData = (InitialData) parameters.get(TASEPUtils.INITIAL_DATA_PARAMETER);
            angle = (Angle) parameters.get(TASEPUtils.ANGLE_PARAMETER);
            jumpRate = (Double) parameters.get(TASEPUtils.JUMP_RATE_PARAMETER);
            particleSize = (Double) parameters.get(TASEPUtils.PARTICLE_SIZE_PARAMETER);
            Dimension windowSize = (Dimension) parameters.get(ModelUtils.SIZE_PARAMETER);

            initializeParticles(windowSize);
        }
    }

    @Override
    public HashMap<String, Object> getDefaultParameters() {
        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put(TASEPUtils.JUMP_RATE_PARAMETER, DEFAULT_JUMP_RATE);
        parameters.put(TASEPUtils.PARTICLE_SIZE_PARAMETER, DEFAULT_PARTICLE_SIZE);
        parameters.put(TASEPUtils.ANGLE_PARAMETER, DEFAULT_ANGLE);
        parameters.put(TASEPUtils.INITIAL_DATA_PARAMETER, DEFAULT_INITIAL_DATA);
        return parameters;
    }

    @Override
    public void initialize() {
    }

    /**
     * Initializes the starting configuration of the particles.
     * In the flat case the number of particles is taken bigger than the width of the window,
     * to make sure that the left border cannot be seen before hitting the top of the window.
     */
    private void initializeParticles(Dimension size) {
        modelTime = 0;
        canBeStopped = false;
        int width = (int) (size.getWidth() / particleSize);
        int height = (int) (size.getHeight() / particleSize);
        switch (initialData) {
            case FLAT:
                int particlesNumber = width / 2 + (int) (height / jumpRate);
                particles = new int[particlesNumber];
                for (int k = 0; k < particles.length; k++) {
                    particles[k] = particlesNumber - 2 * k - 1;
                }
                break;
            case HALF_FLAT:
                particles = new int[width / 4];
                for (int k = 0; k < particles.length; k++) {
                    particles[k] = -2 * k - 1;
                }
                break;
            case STEP:
                particles = new int[width / 2];
                for (int k = 0; k < particles.length; k++) {
                    particles[k] = -k;
                }
                break;
        }
    }

    /**
     * Checks if the particle with the given number can jump, i.e. if the right position is empty.
     *
     * @param index number of a particle.
     * @return {@code true} if the particle can jump and {@code false} otherwise.
     */
    private boolean canJump(int index) {
        return !(particles == null || particles.length == 0 || index >= particles.length)
                && (index == 0 || particles[index - 1] - particles[index] > 1);
    }

    @Override
    public void update() {
        double localTime = 0;
        while (localTime <= 1) {
            localTime += -Math.log(Math.random()) / (jumpRate * particles.length);
            int numberOfJumping = (int) (Math.random() * particles.length);
            if (canJump(numberOfJumping)) {
                particles[numberOfJumping]++;
            }
        }
        modelTime += localTime;
    }

    @Override
    public boolean canStop() {
        return canBeStopped;
    }

    @Override
    public void paint(GraphicsContext graphicsContext) {
        double canvasWidth = graphicsContext.getCanvas().getWidth();
        double canvasHeight = graphicsContext.getCanvas().getHeight();

        graphicsContext.clearRect(0, 0, canvasWidth, canvasHeight);
        graphicsContext.setStroke(Color.BLACK);
        graphicsContext.setLineWidth(AXIS_LINE_WIDTH);
        graphicsContext.strokeLine(0, canvasHeight - particleSize - BOTTOM_MARGIN_HEIGHTS,
                canvasWidth, canvasHeight - particleSize - BOTTOM_MARGIN_HEIGHTS);

        drawTASEP(graphicsContext);
        drawHeights(graphicsContext);
    }

    /**
     * Draws the TASEP particles at the bottom of the canvas.
     */
    private void drawTASEP(GraphicsContext graphicsContext) {
        graphicsContext.setFill(Color.BLUE);
        for (int particle : particles) {
            graphicsContext.fillOval((int) (graphicsContext.getCanvas().getWidth() / 2 + particleSize * particle - particleSize / 2),
                    graphicsContext.getCanvas().getHeight() - particleSize - BOTTOM_MARGIN_TASEP,
                    particleSize, particleSize);
        }
    }

    /**
     * Draws the growth process.
     */
    private void drawHeights(GraphicsContext graphicsContext) {
        switch (angle) {
            case ZERO:
                drawFlatHeights(graphicsContext);
                break;
            case FORTY_FIVE:
                drawObliqueHeights(graphicsContext);
                break;
        }
    }

    /**
     * Draws the growth process for the flat angle.
     */
    private void drawFlatHeights(GraphicsContext graphicsContext) {
        switch (initialData) {
            case HALF_FLAT:
                drawHalfFlatForFlatHeights(graphicsContext);
                break;
            case FLAT:
                drawFlatForFlatHeights(graphicsContext);
                break;
            case STEP:
                drawStepForFlatHeights(graphicsContext);
                break;
        }
        drawCornersForFlatHeights(graphicsContext);
    }

    private void drawHalfFlatForFlatHeights(GraphicsContext graphicsContext) {
        double width = graphicsContext.getCanvas().getWidth();
        double height = graphicsContext.getCanvas().getHeight();
        int Delta2 = (int) (particleSize * modelTime / 2);

        graphicsContext.setStroke(Color.BLACK);
        graphicsContext.strokeLine(width / 2, height - 2 * particleSize - 20, width / 2 + height, height - 2 * particleSize - 20 - height);
        if (jumpRate > 0.5) {
            // draw the flat part
            graphicsContext.setStroke(Color.BLACK);
            graphicsContext.strokeLine(0, height - 2 * particleSize - 20 - Delta2, width / 2, height - 2 * particleSize - 20 - Delta2);
            // Draw the parabola
            graphicsContext.setStroke(Color.RED);
            for (int k = 1 + (int) ((1 - jumpRate) * (1 - jumpRate) * modelTime); k < modelTime / 4; k++) {
                graphicsContext.strokeLine(width / 2 + (int) (particleSize * (modelTime - 2 * Math.sqrt(modelTime * k))), height + (int) (-2 * particleSize - 20 - particleSize * (2 * k + (modelTime - 2 * Math.sqrt(modelTime * k)))), width / 2 + (int) (particleSize * (modelTime - 2 * Math.sqrt(modelTime * (k - 1)))), height + (int) (-2 * particleSize - 20 - particleSize * (2 * k - 2 + (modelTime - 2 * Math.sqrt(modelTime * (k - 1))))));
            }
            // Draw the shock region
            graphicsContext.setStroke(Color.CYAN);
            if (jumpRate < 1) {
                graphicsContext.strokeLine(width / 2 + (int) (particleSize * (2 * jumpRate - 1) * modelTime), height + (int) (-2 * particleSize - 20 - particleSize * (1 - 2 * jumpRate + 2 * jumpRate * jumpRate) * modelTime), width / 2 + (int) (particleSize * (jumpRate * modelTime)), height + (int) (-2 * particleSize - 20 - particleSize * (jumpRate * modelTime)));
            }
        } else {
            // draw the flat part
            graphicsContext.setStroke(Color.RED);
            graphicsContext.strokeLine(0, height - 2 * particleSize - 20 - Delta2, width / 2 - (int) (particleSize * (0.5 - jumpRate) * modelTime), height - 2 * particleSize - 20 - Delta2);
            // Draw the shock region
            graphicsContext.setStroke(Color.CYAN);
            graphicsContext.strokeLine(width / 2 + (int) (particleSize * ((jumpRate - 0.5)) * modelTime), height - 2 * particleSize - 20 - Delta2, width / 2 + (int) (particleSize * (jumpRate * modelTime)), height + (int) (-2 * particleSize - 20 - particleSize * (jumpRate * modelTime)));
        }
    }

    /**
     * Draws the trend line in the case of the flat initial data and flat angle.
     * If the growth process doesn't fit into the window, the algorithm is stopped.
     */
    private void drawFlatForFlatHeights(GraphicsContext graphicsContext) {
        double width = graphicsContext.getCanvas().getWidth();
        double height = graphicsContext.getCanvas().getHeight();
        int trend = (int) (jumpRate * modelTime * particleSize / 2);
        graphicsContext.setStroke(Color.RED);
        graphicsContext.strokeLine(0, height - particleSize - BOTTOM_MARGIN_HEIGHTS - trend,
                width, height - particleSize - BOTTOM_MARGIN_HEIGHTS - trend);
        if (trend >= height) {
            canBeStopped = true;
        }
    }

    private void drawStepForFlatHeights(GraphicsContext graphicsContext) {
        double width = graphicsContext.getCanvas().getWidth();
        double height = graphicsContext.getCanvas().getHeight();

        graphicsContext.setStroke(Color.BLACK);
        graphicsContext.strokeLine(width / 2, height - 2 * particleSize - 20, width / 2 - height, height - 2 * particleSize - 20 - height);
        graphicsContext.strokeLine(width / 2, height - 2 * particleSize - 20, width / 2 + height, height - 2 * particleSize - 20 - height);
// Draw the parabola
        graphicsContext.setStroke(Color.RED);
        for (int k = 1 + (int) ((1 - jumpRate) * (1 - jumpRate) * modelTime); k < modelTime; k++) {
            graphicsContext.strokeLine(width / 2 + (int) (particleSize * (modelTime - 2 * Math.sqrt(modelTime * k))), height + (int) (-2 * particleSize - 20 - particleSize * (2 * k + (modelTime - 2 * Math.sqrt(modelTime * k)))), width / 2 + (int) (particleSize * (modelTime - 2 * Math.sqrt(modelTime * (k - 1)))), height + (int) (-2 * particleSize - 20 - particleSize * (2 * k - 2 + (modelTime - 2 * Math.sqrt(modelTime * (k - 1))))));
        }
// Draw the shock region
        graphicsContext.setStroke(Color.CYAN);
        if (jumpRate < 1) {
            graphicsContext.strokeLine(width / 2 + (int) (particleSize * (2 * jumpRate - 1) * modelTime), height + (int) (-2 * particleSize - 20 - particleSize * (1 - 2 * jumpRate + 2 * jumpRate * jumpRate) * modelTime), width / 2 + (int) (particleSize * (jumpRate * modelTime)), height + (int) (-2 * particleSize - 20 - particleSize * (jumpRate * modelTime)));
        }
    }

    /**
     * Draws the landscape for the flat angle.
     */
    private void drawCornersForFlatHeights(GraphicsContext graphicsContext) {
        graphicsContext.setStroke(Color.BLUE);
        graphicsContext.setLineWidth(STANDARD_LINE_WIDTH);
        for (int particleNumber = 0; particleNumber < particles.length; particleNumber++) {
            drawCorner(graphicsContext, particleNumber);
        }
    }

    /**
     * Draws a corner for the particle with the given number.
     *
     * @param particleNumber the number of the particle whose corner will be drawn.
     */
    private void drawCorner(GraphicsContext graphicsContext, int particleNumber) {
        double width = graphicsContext.getCanvas().getWidth();
        double height = graphicsContext.getCanvas().getHeight();
        double horizontalShift = width / 2 + particleSize * particles[particleNumber],
                verticalShift = height - BOTTOM_MARGIN_HEIGHTS + particleSize * (particles.length - 2 * particleNumber
                        - particles[particleNumber] - 1.5);

        graphicsContext.strokeLine(horizontalShift, verticalShift - particleSize,
                horizontalShift + particleSize, verticalShift);
        if (particleNumber > 0) {
            graphicsContext.strokeLine(horizontalShift + particleSize, verticalShift,
                    width / 2 + particleSize * particles[particleNumber - 1],
                    height - BOTTOM_MARGIN_HEIGHTS - particleSize * (2 * particleNumber + particles[particleNumber - 1] - particles.length + 0.5));
        }
    }

    private void drawCornersForObliqueHeights(GraphicsContext graphicsContext) {
        double width = graphicsContext.getCanvas().getWidth();
        double height = graphicsContext.getCanvas().getHeight();

        graphicsContext.setStroke(Color.BLUE);

        double Dm = (initialData == InitialData.FLAT) ? particleSize * particles.length / 2 : 0;
        if (initialData == InitialData.FLAT) {
            for (int k = particles.length / 2 + 1; k < particles.length; k++) {
                graphicsContext.strokeLine(width / 2 + (int) (particleSize * (particles[k - 1] + k - 1) - Dm), height + (int) (-particleSize * (k + 1) - 20 + Dm), width / 2 + (int) (particleSize * (particles[k] + k - 1) + particleSize - Dm), height + (int) (-2 * particleSize - 20 - particleSize * k + particleSize + Dm));
                graphicsContext.strokeLine(width / 2 + (int) (particleSize * (particles[k] + k) - Dm), height + (int) (-(2 + k - 1) * particleSize - 20 + Dm), width / 2 + (int) (particleSize * (particles[k] + k - 1) + particleSize - Dm), height + (int) (-2 * particleSize - 20 - particleSize * k + Dm));
            }
        }
        if (initialData != InitialData.FLAT) {
            graphicsContext.strokeLine((int) (width / 2 + particleSize * particles[1] + particleSize - Dm), height + (int) (-2 * particleSize - 20 + Dm), (int) (width / 2 + particleSize * particles[1] + particleSize - Dm), height + (int) (-2 * particleSize - 20 - particleSize + Dm));
            for (int k = 2; k < particles.length; k++) {
                graphicsContext.strokeLine((int) (width / 2 + particleSize * (particles[k - 1] + k - 2) + particleSize - Dm), height + (int) (-2 * particleSize - 20 - particleSize * (k - 1) + Dm), (int) (width / 2 + particleSize * (particles[k] + k - 1) + particleSize - Dm), height + (int) (-2 * particleSize - 20 - particleSize * k + particleSize + Dm));
                graphicsContext.strokeLine((int) (width / 2 + particleSize * (particles[k] + k - 1) + particleSize - Dm), height + (int) (-2 * particleSize - 20 - particleSize * k + particleSize + Dm), (int) (width / 2 + particleSize * (particles[k] + k - 1) + particleSize - Dm), height + (int) (-2 * particleSize - 20 - particleSize * k + Dm));
            }
        }
    }

    private void drawObliqueHeights(GraphicsContext graphicsContext) {
        switch (initialData) {
            case HALF_FLAT:
                drawHalfFlatForObliqueHeights(graphicsContext);
                break;
            case FLAT:
                drawFlatForObliqueHeights(graphicsContext);
                break;
            case STEP:
                drawStepForObliqueHeights(graphicsContext);
                break;
        }

        drawCornersForObliqueHeights(graphicsContext);
    }

    private void drawHalfFlatForObliqueHeights(GraphicsContext graphicsContext) {
        double width = graphicsContext.getCanvas().getWidth();
        double height = graphicsContext.getCanvas().getHeight();
        int Delta2 = (int) (particleSize * modelTime / 2);

        if (jumpRate > 0.5) {
            // draw the flat part
            graphicsContext.setStroke(Color.BLACK);
            graphicsContext.strokeLine((int) (width / 2 + particleSize * modelTime / 4), height - (int) (2 * particleSize + particleSize * modelTime / 4) - 20, (int) (width / 2) - height, height - (int) (2 * particleSize) - 20 - height - Delta2);
            // Draw the parabola
            graphicsContext.setStroke(Color.RED);
            for (int k = 1 + (int) ((1 - jumpRate) * (1 - jumpRate) * modelTime); k < modelTime / 4; k++) {
                graphicsContext.strokeLine((int) (width / 2 + particleSize * (modelTime - 2 * Math.sqrt(modelTime * (k - 1)) + k - 1)), height + (int) (-2 * particleSize - 20 - particleSize * (k - 1)), (int) (width / 2 + particleSize * (modelTime - 2 * Math.sqrt(modelTime * k) + k)), height + (int) (-2 * particleSize - 20 - particleSize * k));
            }
            // Draw the shock region
            graphicsContext.setStroke(Color.CYAN);
            if (jumpRate < 1) {
                graphicsContext.strokeLine((int) (width / 2 + particleSize * modelTime * jumpRate * jumpRate), height + (int) (-2 * particleSize - 20 - particleSize * (1 - jumpRate) * (1 - jumpRate) * modelTime), (int) (width / 2 + particleSize * jumpRate * modelTime), height + (int) (-2 * particleSize - 20));
            }
        } else {
            // draw the flat part
            graphicsContext.setStroke(Color.BLACK);
            graphicsContext.strokeLine((int) (width / 2 + particleSize * modelTime * jumpRate / 2), height + (int) (-2 * particleSize - 20 - particleSize * (1 - jumpRate) * modelTime / 2), (int) (width / 2) - height, height - (int) (2 * particleSize) - 20 - height - Delta2);
            graphicsContext.setStroke(Color.CYAN);
            graphicsContext.strokeLine((int) (width / 2 + particleSize * modelTime * jumpRate / 2), height + (int) (-2 * particleSize - 20 - particleSize * (1 - jumpRate) * modelTime / 2), (int) (width / 2 + particleSize * jumpRate * modelTime), height + (int) (-2 * particleSize - 20));
//            g.drawLine(width/2+(int)(Diameter*((alpha-0.5))*t),height-(int)(2*Diameter)-20-Delta2,width/2+(int)(Diameter*(alpha*t)),height+(int)(-2*Diameter-20-Diameter*(alpha*t)));
        }
    }

    private void drawFlatForObliqueHeights(GraphicsContext graphicsContext) {
        double width = graphicsContext.getCanvas().getWidth();
        double height = graphicsContext.getCanvas().getHeight();
        int Delta2 = (int) (particleSize * modelTime / 2);

        double Dm = particleSize * particles.length / 2.0;
        graphicsContext.setStroke(Color.RED);
        graphicsContext.strokeLine(width / 2 - (int) Dm, height + (int) (-2 * particleSize - 20 - Delta2 - Dm), width / 2 + Delta2, height - (int) (2 * particleSize) - 20);
    }

    private void drawStepForObliqueHeights(GraphicsContext graphicsContext) {
        double width = graphicsContext.getCanvas().getWidth();
        double height = graphicsContext.getCanvas().getHeight();

        // Draw the parabola
        graphicsContext.setStroke(Color.RED);
        for (int k = 1 + (int) ((1 - jumpRate) * (1 - jumpRate) * modelTime); k < modelTime; k++) {
            graphicsContext.strokeLine((int) (width / 2 + particleSize * (modelTime - 2 * Math.sqrt(modelTime * (k - 1)) + k - 1)), height + (int) (-2 * particleSize - 20 - particleSize * (k - 1)), (int) (width / 2 + particleSize * (modelTime - 2 * Math.sqrt(modelTime * k) + k)), height + (int) (-2 * particleSize - 20 - particleSize * k));
        }
//Draw the shock region
        graphicsContext.setStroke(Color.CYAN);
        graphicsContext.strokeLine((int) (width / 2 + particleSize * jumpRate * jumpRate * modelTime), height + (int) (-2 * particleSize - 20 - particleSize * (1 - jumpRate) * (1 - jumpRate) * modelTime), (int) (width / 2 + particleSize * jumpRate * modelTime), height + (int) (-2 * particleSize - 20));
    }
}