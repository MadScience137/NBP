package ch.nbodyproblems.input.button;

public sealed interface InputButton permits KeyCode, MouseButton {
    int getCode();
}
