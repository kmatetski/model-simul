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
}
