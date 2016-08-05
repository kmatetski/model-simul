package org.matetski.models.tasep;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import org.matetski.utils.Model;
import org.matetski.utils.ModelUtils;

import java.awt.*;
import java.util.*;

/**
 * An algorithm simulating the TASEP.
 *
 * @author K.Matetski
 */

public class TASEPModel extends Model {

    private final static String CONTROL_GUI_FILE_NAME = "./tasep.fxml";
    private final static String MODEL_NAME = "TASEP";

    private final static double DEFAULT_JUMP_RATE = 0.5;
    private final static double DEFAULT_PARTICLE_SIZE = 2;
    private final static Angle DEFAULT_ANGLE = Angle.ZERO;
    private final static InitialData DEFAULT_INITIAL_DATA = InitialData.FLAT;

    private final static int BOTTOM_MARGIN_TASEP = 10;
    private final static int BOTTOM_MARGIN_HEIGHTS = BOTTOM_MARGIN_TASEP * 2;
    private final static double AXIS_LINE_WIDTH = 0.5;
    private final static double STANDARD_LINE_WIDTH = 1;

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
        initialData = (InitialData) parameters.get(TASEPUtils.INITIAL_DATA_PARAMETER);
        angle = (Angle) parameters.get(TASEPUtils.ANGLE_PARAMETER);
        jumpRate = (Double) parameters.get(TASEPUtils.JUMP_RATE_PARAMETER);
        particleSize = (Double) parameters.get(TASEPUtils.PARTICLE_SIZE_PARAMETER);
        Dimension size = (Dimension) parameters.get(ModelUtils.SIZE_PARAMETER);

        initializeParticles((int) (size.getWidth() / particleSize));
    }

    @Override
    public HashMap<String, Object> getDefaultParameters() {
        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put(TASEPUtils.JUMP_RATE_PARAMETER, Double.valueOf(DEFAULT_JUMP_RATE));
        parameters.put(TASEPUtils.PARTICLE_SIZE_PARAMETER, Double.valueOf(DEFAULT_PARTICLE_SIZE));
        parameters.put(TASEPUtils.ANGLE_PARAMETER, DEFAULT_ANGLE);
        parameters.put(TASEPUtils.INITIAL_DATA_PARAMETER, DEFAULT_INITIAL_DATA);
        return parameters;
    }

    private double jumpRate;
    private int[] particles;
    private boolean[] particlesCanJump;
    private int numberOfFreeParticles;
    private InitialData initialData;
    private Angle angle;
    private double particleSize;
    private double modelTime = 0;

    @Override
    public void initialize() {
    }

    private void initializeParticles(int size) {
        switch (initialData) {
            case FLAT:
                int particlesNumber = size / 2;
                particles = new int[particlesNumber];
                for (int k = 0; k < particles.length; k++) {
                    particles[k] = particlesNumber - 2 * k - 1;
                }
                break;
            case HALF_FLAT:
                particles = new int[size / 4];
                for (int k = 0; k < particles.length; k++) {
                    particles[k] = -2 * k - 1;
                }
                break;
            case STEP:
                particles = new int[size / 2];
                for (int k = 0; k < particles.length; k++) {
                    particles[k] = -k;
                }
                break;
        }
        initializeJumps();
    }

    private void initializeJumps() {
        particlesCanJump = new boolean[particles.length];
        if (particlesCanJump.length > 1) {
            particlesCanJump[0] = true;
            numberOfFreeParticles = 1;
        }
        for (int k = 1; k < particlesCanJump.length; k++) {
            if (particles[k - 1] - particles[k] > 1) {
                particlesCanJump[k] = true;
                numberOfFreeParticles++;
            } else {
                particlesCanJump[k] = false;
            }
        }
    }

    @Override
    public void update() {
        double Dt;
        double TotT = 0;

        while (TotT <= 1) {
            // Determine the time of next jump
            Dt = -Math.log(Math.random()) / (jumpRate + (numberOfFreeParticles - 1));
            // Determine if it is the first particle jumping or not
            if (particles.length > 1 && Math.random() <= jumpRate / (jumpRate + (numberOfFreeParticles - 1))) {
                particles[0]++;
                if (particles[0] - particles[1] == 2) {
                    particlesCanJump[1] = true;
                    numberOfFreeParticles++;
                }
            } else { // choose randomly one of the other particle that can jump
                int Nmove = 1;
                while (Nmove == 1 && particles.length > 0) {
                    int conta = (int) (Math.random() * (particles.length - 1)) + 2;
                    if (conta < particlesCanJump.length && particlesCanJump[conta]) {
                        Nmove = conta;
                    }
                }
                particles[Nmove]++;
                if (particles[Nmove - 1] - particles[Nmove] == 1) {
                    particlesCanJump[Nmove] = false;
                    numberOfFreeParticles--;
                }
                if (Nmove < particles.length - 1 && particles[Nmove] - particles[Nmove + 1] == 2) {
                    particlesCanJump[Nmove + 1] = true;
                    numberOfFreeParticles++;
                }
            }
            modelTime += Dt;
            TotT += Dt;
        }
    }

    @Override
    public void paint(GraphicsContext graphicsContext) {
        double canvasWidth = graphicsContext.getCanvas().getWidth();
        double canvasHeight = graphicsContext.getCanvas().getHeight();

        graphicsContext.clearRect(0, 0, canvasWidth, canvasHeight);

        graphicsContext.setStroke(Color.BLACK);
        graphicsContext.setLineWidth(AXIS_LINE_WIDTH);
        graphicsContext.strokeLine(canvasWidth / 2, 0, canvasWidth / 2, canvasHeight);
        graphicsContext.strokeLine(0, canvasHeight - (int) particleSize - BOTTOM_MARGIN_HEIGHTS,
                canvasWidth, canvasHeight - (int) particleSize - BOTTOM_MARGIN_HEIGHTS);

        drawTASEP(graphicsContext);
        drawHeights(graphicsContext);
    }

    private void drawTASEP(GraphicsContext graphicsContext) {
        graphicsContext.setFill(Color.BLUE);
        for (int k = 0; k < particles.length; k++) {
            graphicsContext.fillOval((int) (graphicsContext.getCanvas().getWidth() / 2 + particleSize * particles[k]),
                    graphicsContext.getCanvas().getHeight() - (int) particleSize - BOTTOM_MARGIN_TASEP,
                    (int) particleSize, (int) particleSize);
        }
    }

    private void drawHeights(GraphicsContext graphicsContext) {
        switch (angle) {
            case ZERO:
                drawZeroHeights(graphicsContext);
                break;
            case FOURTY_FIVE:
                drawAngleHeights(graphicsContext);
                break;
        }
    }

    private void drawZeroHeights(GraphicsContext graphicsContext) {
        double width = graphicsContext.getCanvas().getWidth();
        double height = graphicsContext.getCanvas().getHeight();
        int Delta2, PosFirst;
        double Dm;
        double alphaBis;

        double shift = 1.0; // 1=standard

        alphaBis = jumpRate;

        Delta2 = (int) (particleSize * modelTime / 2);
        PosFirst = (int) (particleSize * modelTime * jumpRate);

        // Draw the TASEP particles position in the (k,x_k+k) plot
        graphicsContext.setStroke(Color.WHITE);
        graphicsContext.strokeText("Continuous time = " + (int) (modelTime), width - 300, 50);
        graphicsContext.setStroke(Color.BLUE);
        for (int k = 1; k < particles.length; k++) {
            graphicsContext.fillOval((int) (shift * width / 2 + particleSize * particles[k]), height - (int) (particleSize) - 10, (int) (particleSize) + 1, (int) (particleSize) + 1);
        }
        if (angle == Angle.ZERO) {
            if (initialData == InitialData.HALF_FLAT) {
                graphicsContext.setStroke(Color.BLACK);
                graphicsContext.strokeLine(width / 2, height - (int) (2 * particleSize) - 20, width / 2 + height, height - (int) (2 * particleSize) - 20 - height);
                if (jumpRate > 0.5) {
                    // draw the flat part
                    graphicsContext.setStroke(Color.BLACK);
                    graphicsContext.strokeLine(0, height - (int) (2 * particleSize) - 20 - Delta2, width / 2, height - (int) (2 * particleSize) - 20 - Delta2);
                    // Draw the parabola
                    graphicsContext.setStroke(Color.RED);
                    for (int k = 1 + (int) ((1 - alphaBis) * (1 - alphaBis) * modelTime); k < modelTime / 4; k++) {
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
                    graphicsContext.strokeLine(0, height - (int) (2 * particleSize) - 20 - Delta2, width / 2 - (int) (particleSize * (0.5 - jumpRate) * modelTime), height - (int) (2 * particleSize) - 20 - Delta2);
                    // Draw the shock region
                    graphicsContext.setStroke(Color.CYAN);
                    graphicsContext.strokeLine(width / 2 + (int) (particleSize * ((jumpRate - 0.5)) * modelTime), height - (int) (2 * particleSize) - 20 - Delta2, width / 2 + (int) (particleSize * (jumpRate * modelTime)), height + (int) (-2 * particleSize - 20 - particleSize * (jumpRate * modelTime)));
                }
            }
            if (initialData == InitialData.FLAT) {
                graphicsContext.setStroke(Color.RED);
                graphicsContext.strokeLine(0, height - (int) (2 * particleSize) - 20 - Delta2, width, height - (int) (2 * particleSize) - 20 - Delta2);
            }
            if (initialData == InitialData.STEP) {
                graphicsContext.setStroke(Color.BLACK);
                graphicsContext.strokeLine(width / 2, height - (int) (2 * particleSize) - 20, width / 2 - height, height - (int) (2 * particleSize) - 20 - height);
                graphicsContext.strokeLine(width / 2, height - (int) (2 * particleSize) - 20, width / 2 + height, height - (int) (2 * particleSize) - 20 - height);
// Draw the parabola
                graphicsContext.setStroke(Color.RED);
                for (int k = 1 + (int) ((1 - alphaBis) * (1 - alphaBis) * modelTime); k < modelTime; k++) {
                    graphicsContext.strokeLine(width / 2 + (int) (particleSize * (modelTime - 2 * Math.sqrt(modelTime * k))), height + (int) (-2 * particleSize - 20 - particleSize * (2 * k + (modelTime - 2 * Math.sqrt(modelTime * k)))), width / 2 + (int) (particleSize * (modelTime - 2 * Math.sqrt(modelTime * (k - 1)))), height + (int) (-2 * particleSize - 20 - particleSize * (2 * k - 2 + (modelTime - 2 * Math.sqrt(modelTime * (k - 1))))));
                }
// Draw the shock region
                graphicsContext.setStroke(Color.CYAN);
                if (jumpRate < 1) {
                    graphicsContext.strokeLine(width / 2 + (int) (particleSize * (2 * jumpRate - 1) * modelTime), height + (int) (-2 * particleSize - 20 - particleSize * (1 - 2 * jumpRate + 2 * jumpRate * jumpRate) * modelTime), width / 2 + (int) (particleSize * (jumpRate * modelTime)), height + (int) (-2 * particleSize - 20 - particleSize * (jumpRate * modelTime)));
                }
//
            }
            graphicsContext.setStroke(Color.BLUE);
            if (initialData == InitialData.FLAT) {
                Dm = particleSize * particles.length * 1.0;
            } else {
                Dm = 0;
            }
            graphicsContext.strokeLine(width / 2 + (int) (particleSize * particles[1] + particleSize), height + (int) (-2 * particleSize - 20 - particleSize * (2 * 1 + particles[1]) + particleSize + Dm), width / 2 + (int) (particleSize * particles[1] + particleSize) + height, height + (int) (-2 * particleSize - 20 - particleSize * (2 * 1 + particles[1]) + particleSize + Dm) - height);
            graphicsContext.strokeLine(width / 2 + (int) (particleSize * particles[1]), height + (int) (-2 * particleSize - 20 - particleSize * (2 * 1 + particles[1]) + Dm), width / 2 + (int) (particleSize * particles[1] + particleSize), height + (int) (-2 * particleSize - 20 - particleSize * (2 * 1 + particles[1]) + particleSize + Dm));
            for (int k = 2; k < particles.length; k++) {
                graphicsContext.strokeLine(width / 2 + (int) (particleSize * particles[k]), height + (int) (-2 * particleSize - 20 - particleSize * (2 * k + particles[k]) + Dm), width / 2 + (int) (particleSize * particles[k] + particleSize), height + (int) (-2 * particleSize - 20 - particleSize * (2 * k + particles[k]) + particleSize + Dm));
                graphicsContext.strokeLine(width / 2 + (int) (particleSize * particles[k] + particleSize), height + (int) (-2 * particleSize - 20 - particleSize * (2 * k + particles[k]) + particleSize + Dm), width / 2 + (int) (particleSize * particles[k - 1]), height + (int) (-2 * particleSize - 20 - particleSize * (2 * k - 2 + particles[k - 1]) + Dm));
            }
        }
        if (angle == Angle.FOURTY_FIVE) {
            if (initialData == InitialData.HALF_FLAT) {
                if (jumpRate > 0.5) {
                    // draw the flat part
                    graphicsContext.setStroke(Color.BLACK);
                    graphicsContext.strokeLine((int) (shift * width / 2 + particleSize * modelTime / 4), height - (int) (2 * particleSize + particleSize * modelTime / 4) - 20, (int) (shift * width / 2) - height, height - (int) (2 * particleSize) - 20 - height - Delta2);
                    // Draw the parabola
                    graphicsContext.setStroke(Color.RED);
                    for (int k = 1 + (int) ((1 - alphaBis) * (1 - alphaBis) * modelTime); k < modelTime / 4; k++) {
                        graphicsContext.strokeLine((int) (shift * width / 2 + particleSize * (modelTime - 2 * Math.sqrt(modelTime * (k - 1)) + k - 1)), height + (int) (-2 * particleSize - 20 - particleSize * (k - 1)), (int) (shift * width / 2 + particleSize * (modelTime - 2 * Math.sqrt(modelTime * k) + k)), height + (int) (-2 * particleSize - 20 - particleSize * k));
                    }
                    // Draw the shock region
                    graphicsContext.setStroke(Color.CYAN);
                    if (jumpRate < 1) {
                        graphicsContext.strokeLine((int) (shift * width / 2 + particleSize * modelTime * jumpRate * jumpRate), height + (int) (-2 * particleSize - 20 - particleSize * (1 - jumpRate) * (1 - jumpRate) * modelTime), (int) (shift * width / 2 + particleSize * jumpRate * modelTime), height + (int) (-2 * particleSize - 20));
                    }
                } else {
                    // draw the flat part
                    graphicsContext.setStroke(Color.BLACK);
                    graphicsContext.strokeLine((int) (shift * width / 2 + particleSize * modelTime * jumpRate / 2), height + (int) (-2 * particleSize - 20 - particleSize * (1 - jumpRate) * modelTime / 2), (int) (shift * width / 2) - height, height - (int) (2 * particleSize) - 20 - height - Delta2);
                    graphicsContext.setStroke(Color.CYAN);
                    graphicsContext.strokeLine((int) (shift * width / 2 + particleSize * modelTime * jumpRate / 2), height + (int) (-2 * particleSize - 20 - particleSize * (1 - jumpRate) * modelTime / 2), (int) (shift * width / 2 + particleSize * jumpRate * modelTime), height + (int) (-2 * particleSize - 20));
//            g.drawLine(width/2+(int)(Diameter*((alpha-0.5))*t),height-(int)(2*Diameter)-20-Delta2,width/2+(int)(Diameter*(alpha*t)),height+(int)(-2*Diameter-20-Diameter*(alpha*t)));
                }
            }
            if (initialData == InitialData.STEP) {
// Draw the parabola
                graphicsContext.setStroke(Color.RED);
                for (int k = 1 + (int) ((1 - alphaBis) * (1 - alphaBis) * modelTime); k < modelTime; k++) {
                    graphicsContext.strokeLine((int) (shift * width / 2 + particleSize * (modelTime - 2 * Math.sqrt(modelTime * (k - 1)) + k - 1)), height + (int) (-2 * particleSize - 20 - particleSize * (k - 1)), (int) (shift * width / 2 + particleSize * (modelTime - 2 * Math.sqrt(modelTime * k) + k)), height + (int) (-2 * particleSize - 20 - particleSize * k));
                }
//Draw the shock region
                graphicsContext.setStroke(Color.CYAN);
                if (jumpRate < 1) {
                    graphicsContext.strokeLine((int) (shift * width / 2 + particleSize * jumpRate * jumpRate * modelTime), height + (int) (-2 * particleSize - 20 - particleSize * (1 - jumpRate) * (1 - jumpRate) * modelTime), (int) (shift * width / 2 + particleSize * jumpRate * modelTime), height + (int) (-2 * particleSize - 20));
                }
//
            }
            if (initialData == InitialData.FLAT) {
                Dm = particleSize * particles.length / 2.0;
                graphicsContext.setStroke(Color.RED);
                graphicsContext.strokeLine(width / 2 - (int) (Dm), height + (int) (-2 * particleSize - 20 - Delta2 - Dm), width / 2 + Delta2, height - (int) (2 * particleSize) - 20);
            }
            graphicsContext.setStroke(Color.BLUE);
            if (initialData == InitialData.FLAT) {
                Dm = particleSize * particles.length / 2.0;
            } else {
                Dm = 0;
            }
            if (initialData == InitialData.FLAT) {
                for (int k = particles.length / 2 + 1; k < particles.length; k++) {
                    graphicsContext.strokeLine(width / 2 + (int) (particleSize * (particles[k - 1] + k - 1) - Dm), height + (int) (-particleSize * (k + 1) - 20 + Dm), width / 2 + (int) (particleSize * (particles[k] + k - 1) + particleSize - Dm), height + (int) (-2 * particleSize - 20 - particleSize * k + particleSize + Dm));
                    graphicsContext.strokeLine(width / 2 + (int) (particleSize * (particles[k] + k) - Dm), height + (int) (-(2 + k - 1) * particleSize - 20 + Dm), width / 2 + (int) (particleSize * (particles[k] + k - 1) + particleSize - Dm), height + (int) (-2 * particleSize - 20 - particleSize * k + Dm));
                }
            }
            if (initialData != InitialData.FLAT) {
                graphicsContext.strokeLine((int) (shift * width / 2 + particleSize * particles[1] + particleSize - Dm), height + (int) (-2 * particleSize - 20 + Dm), (int) (shift * width / 2 + particleSize * particles[1] + particleSize - Dm), height + (int) (-2 * particleSize - 20 - particleSize + Dm));
                for (int k = 2; k < particles.length; k++) {
                    graphicsContext.strokeLine((int) (shift * width / 2 + particleSize * (particles[k - 1] + k - 2) + particleSize - Dm), height + (int) (-2 * particleSize - 20 - particleSize * (k - 1) + Dm), (int) (shift * width / 2 + particleSize * (particles[k] + k - 1) + particleSize - Dm), height + (int) (-2 * particleSize - 20 - particleSize * k + particleSize + Dm));
                    graphicsContext.strokeLine((int) (shift * width / 2 + particleSize * (particles[k] + k - 1) + particleSize - Dm), height + (int) (-2 * particleSize - 20 - particleSize * k + particleSize + Dm), (int) (shift * width / 2 + particleSize * (particles[k] + k - 1) + particleSize - Dm), height + (int) (-2 * particleSize - 20 - particleSize * k + Dm));
                }
            }

        }
        graphicsContext.setStroke(Color.BLACK);
        graphicsContext.strokeLine(0, height - (int) (2 * particleSize) - 20, width, height - (int) (2 * particleSize) - 20);
    }

    /**
     * int globalShift = (int) (particleSize * modelTime / 2);
     * <p>
     * switch (initialData) {
     * case FLAT:
     * graphicsContext.setStroke(Color.RED);
     * graphicsContext.setLineWidth(AXIS_LINE_WIDTH);
     * graphicsContext.strokeLine(0, height - BOTTOM_MARGIN_HEIGHTS - (int) particleSize - globalShift,
     * width, height - BOTTOM_MARGIN_HEIGHTS - (int) particleSize - globalShift);
     * break;
     * case HALF_FLAT:
     * graphicsContext.setFill(Color.BLACK);
     * graphicsContext.strokeLine(width / 2, height - (int) (2 * particleSize) - 20,
     * width / 2 + height, height - (int) (2 * particleSize) - 20 - height);
     * if (jumpRate > 0.5) {
     * // draw the flat part
     * graphicsContext.setFill(Color.BLACK);
     * graphicsContext.strokeLine(0, height - (int) (2 * particleSize) - 20 - globalShift,
     * width / 2, height - (int) (2 * particleSize) - 20 - globalShift);
     * // Draw the parabola
     * graphicsContext.setFill(Color.RED);
     * for (int k = 1 + (int) ((1 - jumpRate) * (1 - jumpRate) * modelTime); k < modelTime / 4; k++) {
     * graphicsContext.strokeLine(width / 2 + (int) (particleSize * (modelTime - 2 * Math.sqrt(modelTime * k))),
     * height + (int) (-2 * particleSize - 20 - particleSize * (2 * k + (modelTime - 2 * Math.sqrt(modelTime * k)))),
     * width / 2 + (int) (particleSize * (modelTime - 2 * Math.sqrt(modelTime * (k - 1)))),
     * height + (int) (-2 * particleSize - 20 - particleSize * (2 * k - 2 + (modelTime - 2 * Math.sqrt(modelTime * (k - 1))))));
     * }
     * // Draw the shock region
     * graphicsContext.setFill(Color.CYAN);
     * if (jumpRate < 1) {
     * graphicsContext.strokeLine(width / 2 + (int) (particleSize * (2 * jumpRate - 1) * modelTime),
     * height + (int) (-2 * particleSize - 20 - particleSize * (1 - 2 * jumpRate + 2 * jumpRate * jumpRate) * modelTime),
     * width / 2 + (int) (particleSize * (jumpRate * modelTime)),
     * height + (int) (-2 * particleSize - 20 - particleSize * (jumpRate * modelTime)));
     * }
     * } else {
     * // draw the flat part
     * graphicsContext.setFill(Color.RED);
     * graphicsContext.strokeLine(0, height - (int) (2 * particleSize) - 20 - globalShift,
     * width / 2 - (int) (particleSize * (0.5 - jumpRate) * modelTime), height - (int) (2 * particleSize) - 20 - globalShift);
     * // Draw the shock region
     * graphicsContext.setFill(Color.CYAN);
     * graphicsContext.strokeLine(width / 2 + (int) (particleSize * ((jumpRate - 0.5)) * modelTime), height - (int) (2 * particleSize) - 20 - globalShift,
     * width / 2 + (int) (particleSize * (jumpRate * modelTime)), height + (int) (-2 * particleSize - 20 - particleSize * (jumpRate * modelTime)));
     * }
     * break;
     * case STEP:
     * graphicsContext.setFill(Color.BLACK);
     * graphicsContext.strokeLine(width / 2, height - (int) (2 * particleSize) - 20, width / 2 - height, height - (int) (2 * particleSize) - 20 - height);
     * graphicsContext.strokeLine(width / 2, height - (int) (2 * particleSize) - 20, width / 2 + height, height - (int) (2 * particleSize) - 20 - height);
     * //Draw the parabola
     * graphicsContext.setFill(Color.RED);
     * for (int k = 1 + (int) ((1 - jumpRate) * (1 - jumpRate) * modelTime); k < modelTime; k++) {
     * graphicsContext.strokeLine(width / 2 + (int) (particleSize * (modelTime - 2 * Math.sqrt(modelTime * k))),
     * height + (int) (-2 * particleSize - 20 - particleSize * (2 * k + (modelTime - 2 * Math.sqrt(modelTime * k)))),
     * width / 2 + (int) (particleSize * (modelTime - 2 * Math.sqrt(modelTime * (k - 1)))),
     * height + (int) (-2 * particleSize - 20 - particleSize * (2 * k - 2 + (modelTime - 2 * Math.sqrt(modelTime * (k - 1))))));
     * }
     * //Draw the shock region
     * graphicsContext.setFill(Color.CYAN);
     * if (jumpRate < 1) {
     * graphicsContext.strokeLine(width / 2 + (int) (particleSize * (2 * jumpRate - 1) * modelTime),
     * height + (int) (-2 * particleSize - 20 - particleSize * (1 - 2 * jumpRate + 2 * jumpRate * jumpRate) * modelTime),
     * width / 2 + (int) (particleSize * (jumpRate * modelTime)), height + (int) (-2 * particleSize - 20 - particleSize * (jumpRate * modelTime)));
     * }
     * break;
     * }
     **/
    //drawCorners(graphicsContext);
    private void drawCorners(GraphicsContext graphicsContext) {
        double width = graphicsContext.getCanvas().getWidth();
        double height = graphicsContext.getCanvas().getHeight();
        graphicsContext.setLineWidth(STANDARD_LINE_WIDTH);
        graphicsContext.setStroke(Color.BLUE);

        for (int particleNumber = 0; particleNumber < particles.length; particleNumber++) {
            if (particleNumber > 0) {
                double coordinateX = width / 2 + particleSize * (particles[particleNumber] - 0.5);
                double coordinateY = height - 2 * particleSize - BOTTOM_MARGIN_HEIGHTS - particleSize * (particles[particleNumber] + 2 * particleNumber);

                graphicsContext.strokeLine(coordinateX, coordinateY, coordinateX + particleSize, coordinateY + particleSize);
                graphicsContext.strokeLine(coordinateX + particleSize, coordinateY + particleSize,
                        width / 2 + particleSize * particles[particleNumber - 1], height - 2 * particleSize - BOTTOM_MARGIN_HEIGHTS - particleSize * (particles[particleNumber] + 2 * particleNumber));
            } else {
            /*graphicsContext.strokeLine(width / 2 + (int) (particleSize * particles[1] + particleSize), heightShift - particleSize * (2 * 1 + particles[1]),
                width / 2 + (int) (particleSize * particles[1] + particleSize) + height, heightShift - particleSize * (2 * 1 + particles[1]) - height);
        graphicsContext.strokeLine(width / 2 + (int) (particleSize * particles[1]), heightShift - particleSize * (2 * 1 + particles[1]),
                width / 2 + (int) (particleSize * particles[1] + particleSize), heightShift - particleSize * (2 * 1 + particles[1]));

        }*/
            }
        }
    }

    private void drawAngleHeights(GraphicsContext graphicsContext) {
        /**if (Vis == 1) {
         if (IC == 1) {
         g.setColor(Color.black);
         g.drawLine(width / 2, height - (int) (2 * Diameter) - 20, width / 2 + height, height - (int) (2 * Diameter) - 20 - height);
         if (alpha > 0.5) {
         // draw the flat part
         g.setColor(Color.black);
         g.drawLine(0, height - (int) (2 * Diameter) - 20 - Delta2, width / 2, height - (int) (2 * Diameter) - 20 - Delta2);
         // Draw the parabola
         g.setColor(Color.red);
         for (int k = 1 + (int) ((1 - alphaBis) * (1 - alphaBis) * t); k < t / 4; k++) {
         g.drawLine(width / 2 + (int) (Diameter * (t - 2 * Math.sqrt(t * k))), height + (int) (-2 * Diameter - 20 - Diameter * (2 * k + (t - 2 * Math.sqrt(t * k)))), width / 2 + (int) (Diameter * (t - 2 * Math.sqrt(t * (k - 1)))), height + (int) (-2 * Diameter - 20 - Diameter * (2 * k - 2 + (t - 2 * Math.sqrt(t * (k - 1))))));
         }
         // Draw the shock region
         g.setColor(Color.cyan);
         if (alpha < 1) {
         g.drawLine(width / 2 + (int) (Diameter * (2 * alpha - 1) * t), height + (int) (-2 * Diameter - 20 - Diameter * (1 - 2 * alpha + 2 * alpha * alpha) * t), width / 2 + (int) (Diameter * (alpha * t)), height + (int) (-2 * Diameter - 20 - Diameter * (alpha * t)));
         }
         } else {
         // draw the flat part
         g.setColor(Color.red);
         g.drawLine(0, height - (int) (2 * Diameter) - 20 - Delta2, width / 2 - (int) (Diameter * (0.5 - alpha) * t), height - (int) (2 * Diameter) - 20 - Delta2);
         // Draw the shock region
         g.setColor(Color.cyan);
         g.drawLine(width / 2 + (int) (Diameter * ((alpha - 0.5)) * t), height - (int) (2 * Diameter) - 20 - Delta2, width / 2 + (int) (Diameter * (alpha * t)), height + (int) (-2 * Diameter - 20 - Diameter * (alpha * t)));
         }
         }
         if (IC == 2) {
         g.setColor(Color.red);
         g.drawLine(0, height - (int) (2 * Diameter) - 20 - Delta2, width, height - (int) (2 * Diameter) - 20 - Delta2);
         }
         if (IC == 0) {
         g.setColor(Color.black);
         g.drawLine(width / 2, height - (int) (2 * Diameter) - 20, width / 2 - height, height - (int) (2 * Diameter) - 20 - height);
         g.drawLine(width / 2, height - (int) (2 * Diameter) - 20, width / 2 + height, height - (int) (2 * Diameter) - 20 - height);
         // Draw the parabola
         g.setColor(Color.red);
         for (int k = 1 + (int) ((1 - alphaBis) * (1 - alphaBis) * t); k < t; k++) {
         g.drawLine(width / 2 + (int) (Diameter * (t - 2 * Math.sqrt(t * k))), height + (int) (-2 * Diameter - 20 - Diameter * (2 * k + (t - 2 * Math.sqrt(t * k)))), width / 2 + (int) (Diameter * (t - 2 * Math.sqrt(t * (k - 1)))), height + (int) (-2 * Diameter - 20 - Diameter * (2 * k - 2 + (t - 2 * Math.sqrt(t * (k - 1))))));
         }
         // Draw the shock region
         g.setColor(Color.cyan);
         if (alpha < 1) {
         g.drawLine(width / 2 + (int) (Diameter * (2 * alpha - 1) * t), height + (int) (-2 * Diameter - 20 - Diameter * (1 - 2 * alpha + 2 * alpha * alpha) * t), width / 2 + (int) (Diameter * (alpha * t)), height + (int) (-2 * Diameter - 20 - Diameter * (alpha * t)));
         }
         //
         }
         g.setColor(Color.blue);
         if (IC == 2) {
         Dm = Diameter * N * 1.0;
         } else {
         Dm = 0;
         }
         g.drawLine(width / 2 + (int) (Diameter * x[1] + Diameter), height + (int) (-2 * Diameter - 20 - Diameter * (2 * 1 + x[1]) + Diameter + Dm), width / 2 + (int) (Diameter * x[1] + Diameter) + height, height + (int) (-2 * Diameter - 20 - Diameter * (2 * 1 + x[1]) + Diameter + Dm) - height);
         g.drawLine(width / 2 + (int) (Diameter * x[1]), height + (int) (-2 * Diameter - 20 - Diameter * (2 * 1 + x[1]) + Dm), width / 2 + (int) (Diameter * x[1] + Diameter), height + (int) (-2 * Diameter - 20 - Diameter * (2 * 1 + x[1]) + Diameter + Dm));
         for (int k = 2; k < N; k++) {
         g.drawLine(width / 2 + (int) (Diameter * x[k]), height + (int) (-2 * Diameter - 20 - Diameter * (2 * k + x[k]) + Dm), width / 2 + (int) (Diameter * x[k] + Diameter), height + (int) (-2 * Diameter - 20 - Diameter * (2 * k + x[k]) + Diameter + Dm));
         g.drawLine(width / 2 + (int) (Diameter * x[k] + Diameter), height + (int) (-2 * Diameter - 20 - Diameter * (2 * k + x[k]) + Diameter + Dm), width / 2 + (int) (Diameter * x[k - 1]), height + (int) (-2 * Diameter - 20 - Diameter * (2 * k - 2 + x[k - 1]) + Dm));
         }
         }**/
    }
}