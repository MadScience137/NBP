package ch.nbodyproblems.input.button;

import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.glfw.GLFW.*;

public enum MouseButton implements InputButton {
    UNKNOWN(-1),

    MOUSE_LEFT(GLFW_MOUSE_BUTTON_LEFT),
    MOUSE_RIGHT(GLFW_MOUSE_BUTTON_RIGHT),
    MOUSE_MIDDLE(GLFW_MOUSE_BUTTON_MIDDLE),
    MOUSE_BUTTON4(GLFW_MOUSE_BUTTON_4),
    MOUSE_BUTTON5(GLFW_MOUSE_BUTTON_5),
    MOUSE_BUTTON6(GLFW_MOUSE_BUTTON_6),
    MOUSE_BUTTON7(GLFW_MOUSE_BUTTON_7),
    MOUSE_BUTTON8(GLFW_MOUSE_BUTTON_8);

    private static final Map<Integer, MouseButton> BY_GLFW_CODE = new HashMap<>();

    static {
        for (MouseButton button : values()) {
            BY_GLFW_CODE.put(button.glfwCode, button);
        }
    }

    private final int glfwCode;

    MouseButton(int glfwCode) {
        this.glfwCode = glfwCode;
    }

    @Override
    public int getCode() {
        return glfwCode;
    }

    public static MouseButton fromCode(int code) {
        return BY_GLFW_CODE.getOrDefault(code, UNKNOWN);
    }
}
