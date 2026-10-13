package ch.nbodyproblems.input.binding;

import ch.nbodyproblems.input.action.InputAction;
import ch.nbodyproblems.input.button.InputButton;

public sealed interface InputBinding permits ButtonBinding, AxisBinding, AxisButtonBinding {
    InputAction action();

    InputButton modifier();

    default boolean hasModifier() {
        return modifier() != null;
    }
}
