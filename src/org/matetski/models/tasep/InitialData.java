package org.matetski.models.tasep;

//TODO: make one enum class with a name

/**
 * @author K.Matetski
 */
public enum InitialData {
    FLAT("Flat"),
    HALF_FLAT("Half flat"),
    STEP("Step");

    private final String name;

    InitialData(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    public int getInitialPosition(int particlesNumber, int index) {
        switch (this) {
            case FLAT:
                return particlesNumber - 2 * index;
            case HALF_FLAT:
                return -2 * index;
            case STEP:
                return -index;
            default:
                return 0;
        }
    }
}
