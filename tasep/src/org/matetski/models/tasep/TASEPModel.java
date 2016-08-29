package org.matetski.models.tasep;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import org.matetski.utils.Model;
import org.matetski.utils.ModelUtils;

import java.awt.*;
import java.util.*;

import static org.matetski.models.tasep.TASEPUtils.*;

/**
 * An algorithm simulating the TASEP model.
 *
 * @author K.Matetski
 */

public class TASEPModel extends Model {

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
        return CONTROL_GUI_FILE_NAME;
    }

    @Override
    public String getModelName() {
        return MODEL_NAME;
    }

    @Override
    public HashMap<String, Object> getParameters() {
        return null;
    }

    @Override
    public void setParameters(HashMap<String, Object> parameters) {
        if (parameters != null) {
            initialData = (InitialData) parameters.get(INITIAL_DATA_PARAMETER);
            angle = (Angle) parameters.get(ANGLE_PARAMETER);
            jumpRate = (Double) parameters.get(JUMP_RATE_PARAMETER);
            particleSize = (Double) parameters.get(PARTICLE_SIZE_PARAMETER);
            Dimension windowSize = (Dimension) parameters.get(ModelUtils.SIZE_PARAMETER);

            initializeParticles(windowSize);
        }
    }

    @Override
    public HashMap<String, Object> getDefaultParameters() {
        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put(JUMP_RATE_PARAMETER, DEFAULT_JUMP_RATE);
        parameters.put(PARTICLE_SIZE_PARAMETER, DEFAULT_PARTICLE_SIZE);
        parameters.put(ANGLE_PARAMETER, DEFAULT_ANGLE);
        parameters.put(INITIAL_DATA_PARAMETER, DEFAULT_INITIAL_DATA);
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
                particles = new int[width / 2 + (int) (height / jumpRate)];
                break;
            case HALF_FLAT:
                //TODO: to put the correct speed
                particles = new int[width / 4 + (int) (height / jumpRate) / 2];
                break;
            case STEP:
                //TODO: to put the correct speed
                particles = new int[width / 2 + (int) (height / jumpRate) / 2];
                break;
        }
        for (int k = 0; k < particles.length; k++) {
            particles[k] = initialData.getInitialPosition(particles.length, k);
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
        double canvasWidth = graphicsContext.getCanvas().getWidth(),
                canvasHeight = graphicsContext.getCanvas().getHeight();

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
        double width = graphicsContext.getCanvas().getWidth(),
                height = graphicsContext.getCanvas().getHeight();
        graphicsContext.setStroke(Color.BLACK);
        graphicsContext.strokeLine(width / 2, height - particleSize - BOTTOM_MARGIN_HEIGHTS,
                width / 2 + height, height - particleSize - BOTTOM_MARGIN_HEIGHTS - height);
        if (jumpRate > 0.5) {
            drawHighRateForHalfFlatForFlatHeights(graphicsContext);
        } else {
            drawLowRateForHalfFlatForFlatHeights(graphicsContext);
        }
    }

    //TODO: review
    private void drawHighRateForHalfFlatForFlatHeights(GraphicsContext graphicsContext) {
        drawFlatPartHighRateForHalfFlatForFlatHeights(graphicsContext);
        drawParabolaPartHighRateForHalfFlatForFlatHeights(graphicsContext);
        drawShockPartHighRateForHalfFlatForFlatHeights(graphicsContext);
    }

    /**
     * Draws the flat part for half flat initial data.
     */
    private void drawFlatPartHighRateForHalfFlatForFlatHeights(GraphicsContext graphicsContext) {
        double width = graphicsContext.getCanvas().getWidth(),
                height = graphicsContext.getCanvas().getHeight(),
                trend = particleSize * modelTime * jumpRate / 2;
        graphicsContext.setStroke(Color.BLACK);
        graphicsContext.strokeLine(0, height - particleSize - BOTTOM_MARGIN_HEIGHTS - trend,
                width / 2, height - particleSize - BOTTOM_MARGIN_HEIGHTS - trend);
        if (trend >= height) {
            canBeStopped = true;
        }
    }

    /**
     * Draws the parabola part for half flat initial data.
     */
    private void drawParabolaPartHighRateForHalfFlatForFlatHeights(GraphicsContext graphicsContext) {
        double width = graphicsContext.getCanvas().getWidth(),
                height = graphicsContext.getCanvas().getHeight();
        graphicsContext.setStroke(Color.RED);
        for (double k = Math.pow(1 - jumpRate, 2) * modelTime; k < modelTime / 4; k++) {
            graphicsContext.strokeLine(width / 2 + particleSize * (modelTime - 2 * Math.sqrt(modelTime * k)),
                    height - particleSize - BOTTOM_MARGIN_HEIGHTS - particleSize * jumpRate * (2 * k + modelTime - 2 * Math.sqrt(modelTime * k)),
                    width / 2 + particleSize * (modelTime - 2 * Math.sqrt(modelTime * (k - 1))),
                    height - particleSize - BOTTOM_MARGIN_HEIGHTS - particleSize * jumpRate * (2 * k - 2 + modelTime - 2 * Math.sqrt(modelTime * (k - 1))));
        }
    }

    /**
     * Draws the shock part for half flat initial data.
     */
    private void drawShockPartHighRateForHalfFlatForFlatHeights(GraphicsContext graphicsContext) {
        double width = graphicsContext.getCanvas().getWidth(),
                height = graphicsContext.getCanvas().getHeight();
        graphicsContext.setStroke(Color.CYAN);
        graphicsContext.strokeLine(width / 2 + particleSize * (2 * jumpRate - 1) * modelTime,
                height - particleSize - BOTTOM_MARGIN_HEIGHTS - particleSize * (1 - 2 * jumpRate + 2 * Math.pow(jumpRate, 2)) * modelTime,
                width / 2 + particleSize * jumpRate * modelTime,
                height - particleSize - BOTTOM_MARGIN_HEIGHTS - particleSize * jumpRate * modelTime);
    }

    //TODO: review
    private void drawLowRateForHalfFlatForFlatHeights(GraphicsContext graphicsContext) {
        double width = graphicsContext.getCanvas().getWidth(),
                height = graphicsContext.getCanvas().getHeight();
        // draw the flat part
        graphicsContext.setStroke(Color.RED);
        graphicsContext.strokeLine(0, height - particleSize - BOTTOM_MARGIN_HEIGHTS - (int) (particleSize * modelTime / 2),
                width / 2 - (int) (particleSize * (0.5 - jumpRate) * modelTime),
                height - particleSize - BOTTOM_MARGIN_HEIGHTS - (int) (particleSize * modelTime / 2));
        // Draw the shock region
        graphicsContext.setStroke(Color.CYAN);
        graphicsContext.strokeLine(width / 2 + (int) (particleSize * ((jumpRate - 0.5)) * modelTime),
                height - particleSize - BOTTOM_MARGIN_HEIGHTS - (int) (particleSize * modelTime / 2),
                width / 2 + (int) (particleSize * (jumpRate * modelTime)),
                height + (int) (-particleSize - BOTTOM_MARGIN_HEIGHTS - particleSize * (jumpRate * modelTime)));
    }


    /**
     * Draws the trend line in the case of the flat initial data and flat angle.
     * If the growth process doesn't fit into the window, the algorithm is stopped.
     */
    private void drawFlatForFlatHeights(GraphicsContext graphicsContext) {
        double width = graphicsContext.getCanvas().getWidth(),
                height = graphicsContext.getCanvas().getHeight(),
                trend = jumpRate * modelTime * particleSize / 2;
        graphicsContext.setStroke(Color.RED);
        graphicsContext.strokeLine(0, height - particleSize - BOTTOM_MARGIN_HEIGHTS - trend,
                width, height - particleSize - BOTTOM_MARGIN_HEIGHTS - trend);
        if (trend >= height) {
            canBeStopped = true;
        }
    }

    /**
     * Draws the growth process for the step initial data and zero angle.
     */
    private void drawStepForFlatHeights(GraphicsContext graphicsContext) {
        double width = graphicsContext.getCanvas().getWidth(),
                height = graphicsContext.getCanvas().getHeight();
        double middleX = width / 2,
                middleY = height - particleSize - BOTTOM_MARGIN_HEIGHTS;
        graphicsContext.setStroke(Color.BLACK);
        graphicsContext.strokeLine(middleX, middleY, middleX - height, middleY - height);
        graphicsContext.strokeLine(middleX, middleY, middleX + height, middleY - height);
        drawParabolaForStepFlatHeights(graphicsContext);
        drawShockForStepFlatHeights(graphicsContext);
    }

    //TODO: review
    // Draw the parabola
    private void drawParabolaForStepFlatHeights(GraphicsContext graphicsContext) {
        double width = graphicsContext.getCanvas().getWidth(),
                height = graphicsContext.getCanvas().getHeight();
        graphicsContext.setStroke(Color.RED);
        for (int k = 1 + (int) ((1 - jumpRate) * (1 - jumpRate) * modelTime); k < modelTime; k++) {
            graphicsContext.strokeLine(width / 2 + (int) (particleSize * (modelTime - 2 * Math.sqrt(modelTime * k))),
                    height + (int) (-particleSize - BOTTOM_MARGIN_HEIGHTS - particleSize * (2 * k + (modelTime - 2 * Math.sqrt(modelTime * k)))),
                    width / 2 + (int) (particleSize * (modelTime - 2 * Math.sqrt(modelTime * (k - 1)))),
                    height + (int) (-particleSize - BOTTOM_MARGIN_HEIGHTS - particleSize * (2 * k - 2 + (modelTime - 2 * Math.sqrt(modelTime * (k - 1))))));
        }
    }

    //TODO: review
    // Draw the shock region
    private void drawShockForStepFlatHeights(GraphicsContext graphicsContext) {
        double width = graphicsContext.getCanvas().getWidth(),
                height = graphicsContext.getCanvas().getHeight();
        graphicsContext.setStroke(Color.CYAN);
        graphicsContext.strokeLine(width / 2 + (int) (particleSize * (2 * jumpRate - 1) * modelTime),
                height + (int) (-particleSize - BOTTOM_MARGIN_HEIGHTS - particleSize * (1 - 2 * jumpRate + 2 * jumpRate * jumpRate) * modelTime),
                width / 2 + (int) (particleSize * (jumpRate * modelTime)),
                height + (int) (-particleSize - BOTTOM_MARGIN_HEIGHTS - particleSize * (jumpRate * modelTime)));
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
        double width = graphicsContext.getCanvas().getWidth(),
                height = graphicsContext.getCanvas().getHeight();
        // We need this condition to make the central particle be at height 0.
        double centering = (initialData == InitialData.FLAT) ? particles.length : 0;
        double horizontalShift = width / 2 + particleSize * (particles[particleNumber] - 1),
                verticalShift = height - BOTTOM_MARGIN_HEIGHTS
                        - particleSize * (particles[particleNumber] + 2 * particleNumber - centering + 1);

        graphicsContext.strokeLine(horizontalShift, verticalShift - particleSize,
                horizontalShift + particleSize, verticalShift);
        if (particleNumber > 0) {
            graphicsContext.strokeLine(horizontalShift + particleSize, verticalShift,
                    width / 2 + particleSize * (particles[particleNumber - 1] - 1),
                    height - BOTTOM_MARGIN_HEIGHTS
                            - particleSize * (particles[particleNumber - 1] + 2 * (particleNumber - 1) - centering + 2));
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