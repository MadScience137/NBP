package ch.nbodyproblems.input.binding;

import ch.nbodyproblems.input.axis.AxisDirection;
import ch.nbodyproblems.input.button.InputButton;
import ch.nbodyproblems.input.action.AxisInputAction;
import ch.nbodyproblems.input.axis.activation.ActivationFunction;

import static ch.nbodyproblems.input.axis.AxisDirection.POSITIVE;

public record AxisButtonBinding(AxisInputAction action, InputButton button, AxisDirection direction, ActivationFunction activationFunction, double sensitivity, InputButton modifier) implements InputBinding {
    public AxisButtonBinding(AxisInputAction action, InputButton button, AxisDirection direction, ActivationFunction activationFunction, double sensitivity) {
        this(action, button, direction, activationFunction, sensitivity, null);
    }

    public double directionFactor() {
        return direction == POSITIVE ? 1 : -1;
    }
}
