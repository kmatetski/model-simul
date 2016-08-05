package org.matetski.models.tasep;

/**
 * Created by k.matetski on 7/17/16.
 */
//TODO: make one enum class with a name
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
