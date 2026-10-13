package ch.nbodyproblems.input.binding;

public enum CursorRequirement {
    ANY,
    FREE,
    CAPTURED;

    public boolean matches(boolean mouseCaptured) {
        return switch (this) {
            case ANY -> true;
            case FREE -> !mouseCaptured;
            case CAPTURED -> mouseCaptured;
        };
    }
}
