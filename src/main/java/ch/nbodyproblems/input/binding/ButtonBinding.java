package ch.nbodyproblems.input.binding;

import ch.nbodyproblems.input.action.TriggerInputAction;
import ch.nbodyproblems.input.axis.activation.ActivationType;
import ch.nbodyproblems.input.button.InputButton;

public record ButtonBinding(TriggerInputAction action, InputButton button, ActivationType activationType, InputButton modifier) implements InputBinding {
    public ButtonBinding(TriggerInputAction action, InputButton button, ActivationType activationType) {
        this(action, button, activationType, null);
    }
}
