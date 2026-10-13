package ch.nbodyproblems.input;

import ch.nbodyproblems.input.action.InputAction;

import static ch.nbodyproblems.input.action.AxisInputAction.*;
import static ch.nbodyproblems.input.action.TriggerInputAction.*;

public enum InputContext {
    EDITOR(
            CAMERA_ROTATE_PITCH,
            CAMERA_ROTATE_YAW,

            CAMERA_MOVE_LONGITUDINAL,
            CAMERA_MOVE_LATERAL,
            CAMERA_MOVE_VERTICAL,

            CAMERA_RESET
    );

    private final InputAction[] actions;

    InputContext(InputAction... actions) {
        this.actions = actions;
    }

    public boolean containsAction(InputAction action) {
        for (InputAction a : actions) if (action == a) return true;
        return false;
    }
}
