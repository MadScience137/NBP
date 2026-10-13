package ch.nbodyproblems.input;

import ch.nbodyproblems.input.action.InputAction;
import lombok.Getter;

import static ch.nbodyproblems.input.action.AxisInputAction.*;
import static ch.nbodyproblems.input.action.TriggerInputAction.*;

public enum InputContext {
    GLOBAL(false),
    EDITOR(false),
    EDITOR_VIEW(EDITOR, true,
            CAMERA_ROTATE_PITCH,
            CAMERA_ROTATE_YAW,

            CAMERA_MOVE_LONGITUDINAL,
            CAMERA_MOVE_LATERAL,
            CAMERA_MOVE_VERTICAL,

            CAMERA_RESET,

            MOUSE_CAPTURE_TOGGLE,
            MOUSE_CAPTURE_RELEASE
    );

    @Getter
    private InputContext parent;
    @Getter
    private final boolean mouseCaptureEnabled;
    private final InputAction[] actions;

    InputContext(InputContext parent, boolean mouseCaptureEnabled, InputAction... actions) {
        this.parent = parent;
        this.mouseCaptureEnabled = mouseCaptureEnabled;
        this.actions = actions;
    }

    InputContext(boolean mouseCaptureEnabled, InputAction... actions) {
        this(null, mouseCaptureEnabled, actions);
    }

    static {
        for (InputContext value : InputContext.values()) {
            if (value.parent == null && value != GLOBAL) {
                value.parent = GLOBAL;
            }
        }
    }

    public boolean containsAction(InputAction action) {
        return actionsContain(action) || hasParent() && parent.containsAction(action);
    }

    private boolean actionsContain(InputAction action) {
        for (InputAction a : actions) if (action == a) return true;
        return false;
    }

    public boolean hasParent() {
        return parent != null;
    }
}
