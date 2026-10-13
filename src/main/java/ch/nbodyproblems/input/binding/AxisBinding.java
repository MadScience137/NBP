package ch.nbodyproblems.input.binding;

import ch.nbodyproblems.input.MouseAxis;
import ch.nbodyproblems.input.action.AxisInputAction;
import ch.nbodyproblems.input.button.InputButton;

public record AxisBinding(AxisInputAction action, MouseAxis mouseAxis, float sensitivity, boolean invert, InputButton modifier) implements InputBinding {
    public AxisBinding(AxisInputAction action, MouseAxis mouseAxis, float sensitivity, boolean invert) {
        this(action, mouseAxis, sensitivity, invert, null);
    }

    public double directionFactor() {
        return invert ? -1 : 1;
    }
}
