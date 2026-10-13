package ch.nbodyproblems.input.binding;

import ch.nbodyproblems.input.MouseAxis;
import ch.nbodyproblems.input.activation.ActivationFunction;
import ch.nbodyproblems.input.activation.ExponentialActivation;
import ch.nbodyproblems.input.activation.LinearActivation;

import java.util.List;

import static ch.nbodyproblems.input.AxisDirection.NEGATIVE;
import static ch.nbodyproblems.input.AxisDirection.POSITIVE;
import static ch.nbodyproblems.input.activation.ActivationType.*;
import static ch.nbodyproblems.input.button.KeyCode.*;
import static ch.nbodyproblems.input.action.AxisInputAction.*;
import static ch.nbodyproblems.input.action.TriggerInputAction.*;
import static ch.nbodyproblems.input.CursorRequirement.*;
import static ch.nbodyproblems.input.button.MouseButton.*;

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
