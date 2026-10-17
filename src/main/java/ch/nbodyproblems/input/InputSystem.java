package ch.nbodyproblems.input;

import ch.nbodyproblems.input.action.AxisInputAction;
import ch.nbodyproblems.input.action.InputAction;
import ch.nbodyproblems.input.action.TriggerInputAction;
import ch.nbodyproblems.input.axis.MouseAxis;
import ch.nbodyproblems.input.axis.activation.ActivationType;
import ch.nbodyproblems.input.binding.AxisBinding;
import ch.nbodyproblems.input.binding.AxisButtonBinding;
import ch.nbodyproblems.input.binding.ButtonBinding;
import ch.nbodyproblems.input.binding.InputBinding;
import ch.nbodyproblems.input.button.InputButton;
import ch.nbodyproblems.input.button.KeyCode;
import ch.nbodyproblems.input.button.MouseButton;
import ch.nbodyproblems.input.defaults.DefaultBindings;
import ch.nbodyproblems.rendering.scene.Tickable;
import com.spinyowl.legui.system.context.CallbackKeeper;
import lombok.Getter;
import lombok.Setter;
import org.joml.Vector2d;

import java.util.*;

import static org.lwjgl.glfw.GLFW.*;

public class InputSystem implements Tickable {
    private static final double EPSILON = 1e-5;
    private static final float TRIGGER_HOLD_TIME = 0.5f;

    private final long window;
    @Getter
    private boolean mouseCaptured;
    private boolean cursorModeChanging;
    private boolean mousePositionValid;
    private boolean focused;

    private final Set<TriggerInputAction> activeActions = new HashSet<>();
    private final Set<InputButton> pressedButtons = new HashSet<>();
    private final Set<InputButton> releasedButtons = new HashSet<>();
    private final Map<InputButton, Double> activeButtonTimes = new HashMap<>();
    private final Map<InputButton, Set<ButtonBinding>> buttonBindings = new HashMap<>();
    private final Map<InputButton, Set<AxisButtonBinding>> axisButtonBindings = new HashMap<>();
    private final Map<MouseAxis, Set<AxisBinding>> axisBindings = new EnumMap<>(MouseAxis.class);
    private final Map<AxisInputAction, Double> inputAxis = new EnumMap<>(AxisInputAction.class);
    private final Map<InputAction, Set<InputBinding>> bindings = new HashMap<>();

    private InputContext activeContext;
    @Setter
    private InputContext guiContext;
    private InputContext requestedContext;
    private final HashMap<InputAction, Set<Runnable>> onActionCallback = new HashMap<>();

    private final Vector2d mousePosition = new Vector2d();

    public InputSystem(long window) {
        this(window, null);
    }

    /**
     * Adds input handlers to the same callback chains used by the GUI, regardless of GUI focus.
     */
    public InputSystem(long window, CallbackKeeper callbacks) {
        this.window = window;
        focused = glfwGetWindowAttrib(window, GLFW_FOCUSED) == GLFW_TRUE;
        setBindings(DefaultBindings.BINDINGS);

        callbacks.getChainKeyCallback().add(this::handleKey);
        callbacks.getChainCursorPosCallback().add(this::handleMouseMove);
        callbacks.getChainMouseButtonCallback().add(this::handleMouseButton);
        callbacks.getChainScrollCallback().add(this::handleScroll);
        callbacks.getChainWindowFocusCallback().add(this::handleFocus);

        if (glfwRawMouseMotionSupported()) {
            glfwSetInputMode(window, GLFW_RAW_MOUSE_MOTION, GLFW_TRUE);
        }
    }

    public void setMouseCaptured(boolean captured) {
        if (mouseCaptured == captured || (captured && !focused)) return;

        cursorModeChanging = true;
        mousePositionValid = false;
        clearAxisActivations();
        if (captured) {
            guiContext = null;
        }
        try {
            glfwSetInputMode(window, GLFW_CURSOR, captured ? GLFW_CURSOR_DISABLED : GLFW_CURSOR_NORMAL);
            mouseCaptured = captured;
        } finally {
            cursorModeChanging = false;
            mousePositionValid = false;
        }
    }

    private void handleFocus(long win, boolean focused) {
        this.focused = focused;
        mousePositionValid = false;

        if (!focused) {
            setMouseCaptured(false);
            releaseKeys();
            clearAxisActivations();
        }
    }

    @Override
    public void tick(double time, double deltaTime) {
        activatePressBindings();
        activateReleaseBindings();

        double now = System.nanoTime() / 1e9;

        for (Map.Entry<InputButton, Double> entry : activeButtonTimes.entrySet()) {
            double timeHeld = (float) (now - entry.getValue());

            if (TRIGGER_HOLD_TIME <= timeHeld && buttonBindings.containsKey(entry.getKey())) {
                for (ButtonBinding binding : buttonBindings.get(entry.getKey())) {
                    if (binding.activationType() == ActivationType.HOLD && !requiredModifierMissing(binding)) {
                        onActionCallback.get(binding.action()).forEach(Runnable::run);
                        activeActions.add(binding.action());
                    }
                }
            }

            if (axisButtonBindings.containsKey(entry.getKey())) {
                for (AxisButtonBinding binding : axisButtonBindings.get(entry.getKey())) {
                    if (requiredModifierMissing(binding)) continue;

                    double delta = binding.activationFunction().apply(timeHeld)
                            * binding.sensitivity()
                            * deltaTime
                            * binding.directionFactor();

                    inputAxis.merge(binding.action(), delta, Double::sum);
                }
            }
        }
    }


    private void handleMouseMove(long win, double x, double y) {
        if (cursorModeChanging || !focused) return;

        if (!mousePositionValid) {
            mousePosition.set(x, y);
            mousePositionValid = true;
            return;
        }

        double axisDeltaX = mousePosition.x - x;

        if (Math.abs(axisDeltaX) > EPSILON && axisBindings.containsKey(MouseAxis.MOUSE_X)) {
            for (AxisBinding binding : axisBindings.get(MouseAxis.MOUSE_X)) {
                if (!axisBindingEligible(binding)) continue;
                double axisInput = Math.toRadians(axisDeltaX) * binding.sensitivity() * binding.directionFactor();
                inputAxis.merge(binding.action(), axisInput, Double::sum);
            }
        }

        double axisDeltaY = mousePosition.y - y;

        if (Math.abs(axisDeltaY) > EPSILON && axisBindings.containsKey(MouseAxis.MOUSE_Y)) {
            for (AxisBinding binding : axisBindings.get(MouseAxis.MOUSE_Y)) {
                if (!axisBindingEligible(binding)) continue;
                double axisInput = Math.toRadians(axisDeltaY) * binding.sensitivity() * binding.directionFactor();
                inputAxis.merge(binding.action(), axisInput, Double::sum);
            }
        }

        mousePosition.x = x;
        mousePosition.y = y;
    }

    private void handleMouseButton(long win, int button, int action, int mods) {
        MouseButton mouseButton = MouseButton.fromCode(button);

        if (action == GLFW_PRESS) {
            activeButtonTimes.put(mouseButton, System.nanoTime() / 1e9);
            pressedButtons.add(mouseButton);
        } else if (action == GLFW_RELEASE) {
            activeButtonTimes.remove(mouseButton);
            releasedButtons.add(mouseButton);
        }
    }

    private void activateReleaseBindings() {
        for (InputButton button : pressedButtons) {
            if (!buttonBindings.containsKey(button)) continue;

            for (ButtonBinding binding : buttonBindings.get(button)) {
                activeActions.remove(binding.action());

                if (binding.activationType() == ActivationType.RELEASE
                        && onActionCallback.containsKey(binding.action())
                        && !requiredModifierMissing(binding)) {
                    onActionCallback.get(binding.action()).forEach(Runnable::run);
                }
            }
        }
    }

    private void activatePressBindings() {
        for (InputButton button : pressedButtons) {
            if (!buttonBindings.containsKey(button)) continue;

            for (ButtonBinding binding : buttonBindings.get(button)) {
                if (binding.activationType() != ActivationType.PRESS || requiredModifierMissing(binding)) continue;

                activeActions.add(binding.action());
                if (onActionCallback.containsKey(binding.action())) {
                    onActionCallback.get(binding.action()).forEach(Runnable::run);
                }
            }
        }
    }

    private void handleScroll(long win, double xOffset, double yOffset) {
        if (Math.abs(yOffset) > EPSILON && axisBindings.containsKey(MouseAxis.SCROLL_Y)) {
            for (AxisBinding binding : axisBindings.get(MouseAxis.SCROLL_Y)) {
                if (!axisBindingEligible(binding)) continue;
                double axisInput = yOffset * binding.sensitivity() * binding.directionFactor();
                inputAxis.merge(binding.action(), axisInput, Double::sum);
            }
        }

        if (Math.abs(xOffset) > EPSILON && axisBindings.containsKey(MouseAxis.SCROLL_X)) {
            for (AxisBinding binding : axisBindings.get(MouseAxis.SCROLL_X)) {
                if (!axisBindingEligible(binding)) continue;
                double axisInput = xOffset * binding.sensitivity() * binding.directionFactor();
                inputAxis.merge(binding.action(), axisInput, Double::sum);
            }
        }
    }

    private void handleKey(long win, int key, int scancode, int action, int mods) {
        KeyCode keyCode = KeyCode.fromCode(key);
        if (action == GLFW_PRESS) {
            activeButtonTimes.put(keyCode, System.nanoTime() / 1e9);
            pressedButtons.add(keyCode);
        } else if (action == GLFW_RELEASE) {
            activeButtonTimes.remove(keyCode);
            releasedButtons.add(keyCode);
        }
    }

    public void executeContextSwitch() {
        InputContext newContext = guiContext != null ? guiContext : requestedContext;
        if (newContext == null || newContext == activeContext) return;

        if (!newContext.isMouseCaptureEnabled()) {
            setMouseCaptured(false);
        }

        mousePositionValid = false;
        activeContext = newContext;
        buttonBindings.clear();
        axisButtonBindings.clear();
        axisBindings.clear();

        for (Set<InputBinding> bindingSet : bindings.values()) {
            for (InputBinding binding : bindingSet) {
                if (newContext.containsAction(binding.action())) {
                    activateBinding(binding);
                }
            }
        }
    }

    public void requestContextSwitch(InputContext context) {
        requestedContext = context;
    }

    private void activateBinding(InputBinding binding) {
        switch (binding) {
            case ButtonBinding buttonBinding ->
                    buttonBindings.computeIfAbsent(buttonBinding.button(), this::newSet).add(buttonBinding);
            case AxisButtonBinding axisButtonBinding ->
                    axisButtonBindings.computeIfAbsent(axisButtonBinding.button(), this::newSet).add(axisButtonBinding);
            case AxisBinding axisBinding ->
                    axisBindings.computeIfAbsent(axisBinding.mouseAxis(), this::newSet).add(axisBinding);
        }
    }

    public void setBindings(Collection<InputBinding> bindings) {
        this.bindings.clear();
        bindings.forEach(binding ->
                this.bindings.computeIfAbsent(binding.action(), this::newSet).add(binding)
        );
        if (activeContext != null) {
            requestContextSwitch(activeContext);
        }
    }

    public Set<InputBinding> getBindings(InputAction action) {
        return bindings.get(action);
    }

    private void addBinding(InputBinding binding) {
        bindings.computeIfAbsent(binding.action(), this::newSet).add(binding);
        if (activeContext.containsAction(binding.action())) {
            activateBinding(binding);
        }
    }

    private void removeBinding(InputBinding binding) {
        bindings.remove(binding.action());
        requestContextSwitch(activeContext);
    }

    private boolean requiredModifierMissing(InputBinding binding) {
        return binding.hasModifier() && !activeButtonTimes.containsKey(binding.modifier());
    }

    private boolean axisBindingEligible(AxisBinding binding) {
        return binding.cursorRequirement().matches(mouseCaptured) && !requiredModifierMissing(binding);
    }

    public double getAxisActivation(AxisInputAction action) {
        return inputAxis.getOrDefault(action, 0.0);
    }

    public void clearAxisActivations() {
        inputAxis.clear();
    }

    public void clearPressAndReleaseActivations() {
        pressedButtons.clear();
        releasedButtons.clear();
    }

    public void addOnAction(InputAction action, Runnable callback) {
        onActionCallback.computeIfAbsent(action, this::newSet).add(callback);
    }

    public void removeOnAction(InputAction action, Runnable callback) {
        if (onActionCallback.containsKey(action)) {
            onActionCallback.get(action).remove(callback);
        }
    }

    public boolean isActive(TriggerInputAction action) {
        return activeActions.contains(action);
    }

    private void releaseKeys() {
        activeButtonTimes.clear();
        activeActions.clear();
    }

    private <T, R> Set<R> newSet(T key) {
        return new HashSet<>();
    }
}
