package ch.nbodyproblems.input.axis;

public enum AxisDirection {
    POSITIVE(1),
    NEGATIVE(-1);

    public final float dir;

    AxisDirection(float dir) {
        this.dir = dir;
    }
}
