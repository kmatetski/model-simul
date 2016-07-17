package org.matetski.models.tasep;

/**
 * Created by k.matetski on 7/17/16.
 */
public enum Angle {
    ZERO("0%"),
    FOURTY_FIVE("45%");

    public static Angle getDefaultValue() {
        return ZERO;
    }

    private final String name;

    Angle(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    public double getAngle() {
        switch (this) {
            case ZERO:
                return 0;
            case FOURTY_FIVE:
                return Math.PI / 4;
            default:
                return 0;
        }
    }
}
