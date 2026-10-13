package ch.nbodyproblems.input.defaults;

import ch.nbodyproblems.input.axis.MouseAxis;
import ch.nbodyproblems.input.axis.activation.ActivationFunction;
import ch.nbodyproblems.input.axis.activation.ExponentialActivation;
import ch.nbodyproblems.input.binding.AxisBinding;
import ch.nbodyproblems.input.binding.AxisButtonBinding;
import ch.nbodyproblems.input.binding.ButtonBinding;
import ch.nbodyproblems.input.binding.InputBinding;

import java.util.List;

import static ch.nbodyproblems.input.action.AxisInputAction.*;
import static ch.nbodyproblems.input.action.TriggerInputAction.*;
import static ch.nbodyproblems.input.axis.AxisDirection.NEGATIVE;
import static ch.nbodyproblems.input.axis.AxisDirection.POSITIVE;
import static ch.nbodyproblems.input.axis.activation.ActivationType.HOLD;
import static ch.nbodyproblems.input.axis.activation.ActivationType.PRESS;
import static ch.nbodyproblems.input.binding.CursorRequirement.CAPTURED;
import static ch.nbodyproblems.input.binding.CursorRequirement.FREE;
import static ch.nbodyproblems.input.button.KeyCode.*;
import static ch.nbodyproblems.input.button.MouseButton.MOUSE_LEFT;

public final class DefaultBindings {
    private static final float CAMERA_LOOK_SENSITIVITY = 100;
    private static final float CAMERA_MOUSE_SENSITIVITY = 5;
    private static final float CAMERA_MOVE_SENSITIVITY = 5;
    private static final ActivationFunction CAMERA_CONTROL_ACTIVATION = new ExponentialActivation(0.2, 1.5);

    public static final List<InputBinding> BINDINGS = List.of(
            new AxisBinding(CAMERA_ROTATE_PITCH, MouseAxis.MOUSE_Y, CAMERA_MOUSE_SENSITIVITY, false, MOUSE_LEFT, FREE),
            new AxisBinding(CAMERA_ROTATE_YAW, MouseAxis.MOUSE_X, CAMERA_MOUSE_SENSITIVITY, false, MOUSE_LEFT, FREE),
            new AxisBinding(CAMERA_ROTATE_PITCH, MouseAxis.MOUSE_Y, CAMERA_MOUSE_SENSITIVITY, false, null, CAPTURED),
            new AxisBinding(CAMERA_ROTATE_YAW, MouseAxis.MOUSE_X, CAMERA_MOUSE_SENSITIVITY, false, null, CAPTURED),
            new AxisButtonBinding(CAMERA_ROTATE_PITCH, UP, POSITIVE, CAMERA_CONTROL_ACTIVATION, CAMERA_LOOK_SENSITIVITY),
            new AxisButtonBinding(CAMERA_ROTATE_PITCH, DOWN, NEGATIVE, CAMERA_CONTROL_ACTIVATION, CAMERA_LOOK_SENSITIVITY),
            new AxisButtonBinding(CAMERA_ROTATE_YAW, LEFT, POSITIVE, CAMERA_CONTROL_ACTIVATION, CAMERA_LOOK_SENSITIVITY),
            new AxisButtonBinding(CAMERA_ROTATE_YAW, RIGHT, NEGATIVE, CAMERA_CONTROL_ACTIVATION, CAMERA_LOOK_SENSITIVITY),

            new AxisButtonBinding(CAMERA_MOVE_LONGITUDINAL, W, POSITIVE, CAMERA_CONTROL_ACTIVATION, CAMERA_MOVE_SENSITIVITY),
            new AxisButtonBinding(CAMERA_MOVE_LONGITUDINAL, S, NEGATIVE, CAMERA_CONTROL_ACTIVATION, CAMERA_MOVE_SENSITIVITY),
            new AxisButtonBinding(CAMERA_MOVE_LATERAL, A, POSITIVE, CAMERA_CONTROL_ACTIVATION, CAMERA_MOVE_SENSITIVITY),
            new AxisButtonBinding(CAMERA_MOVE_LATERAL, D, NEGATIVE, CAMERA_CONTROL_ACTIVATION, CAMERA_MOVE_SENSITIVITY),
            new AxisButtonBinding(CAMERA_MOVE_VERTICAL, Q, POSITIVE, CAMERA_CONTROL_ACTIVATION, CAMERA_MOVE_SENSITIVITY),
            new AxisButtonBinding(CAMERA_MOVE_VERTICAL, E, NEGATIVE, CAMERA_CONTROL_ACTIVATION, CAMERA_MOVE_SENSITIVITY),

            new ButtonBinding(CAMERA_RESET, R, HOLD),
            new ButtonBinding(MOUSE_CAPTURE_TOGGLE, F1, PRESS),
            new ButtonBinding(MOUSE_CAPTURE_RELEASE, ESCAPE, PRESS)
    );

    private DefaultBindings() {
    }
}
