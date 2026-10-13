package ch.nbodyproblems.input;

import ch.nbodyproblems.input.action.AxisInputAction;
import ch.nbodyproblems.input.action.InputAction;
import ch.nbodyproblems.input.action.TriggerInputAction;
import ch.nbodyproblems.input.activation.ActivationType;
import ch.nbodyproblems.input.binding.*;
import ch.nbodyproblems.input.button.InputButton;
import ch.nbodyproblems.input.button.KeyCode;
import ch.nbodyproblems.input.button.MouseButton;
import ch.nbodyproblems.rendering.scene.Tickable;
import org.joml.Vector2d;

import java.util.*;

import static org.lwjgl.glfw.GLFW.*;

public class InputSystem implements Tickable {
    private static final double EPSILON = 1e-5;
    private static final float TRIGGER_HOLD_TIME = 0.5f;

    private final Set<TriggerInputAction> activeActions = new HashSet<>();
    private final Map<InputButton, Double> activeButtonTimes = new HashMap<>();
    private final Map<InputButton, Set<ButtonBinding>> buttonBindings = new HashMap<>();
    private final Map<InputButton, Set<AxisButtonBinding>> axisButtonBindings = new HashMap<>();
    private final Map<MouseAxis, Set<AxisBinding>> axisBindings = new EnumMap<>(MouseAxis.class);
    private final Map<AxisInputAction, Double> inputAxis = new EnumMap<>(AxisInputAction.class);
    private final Map<InputAction, Set<InputBinding>> bindings = new HashMap<>();

    private InputContext activeContext;
    private final HashMap<InputAction, Set<Runnable>> onActionCallback = new HashMap<>();

    private final Vector2d mousePosition = new Vector2d();

    public InputSystem(long window) {
        setBindings(DefaultBindings.BINDINGS);
        glfwSetKeyCallback(window, this::handleKey);
        glfwSetCursorPosCallback(window, this::handleMouseMove);
        glfwSetMouseButtonCallback(window, this::handleMouseButton);
        glfwSetScrollCallback(window, this::handleScroll);
    }

    @Override
    public void tick(double time, double deltaTime) {
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
        double axisDeltaX = mousePosition.x - x;

        if (Math.abs(axisDeltaX) > EPSILON && axisBindings.containsKey(MouseAxis.MOUSE_X)) {
            for (AxisBinding binding : axisBindings.get(MouseAxis.MOUSE_X)) {
                if (requiredModifierMissing(binding)) continue;
                double axisInput = Math.toRadians(axisDeltaX) * binding.sensitivity() * binding.directionFactor();
                inputAxis.merge(binding.action(), axisInput, Double::sum);
            }
        }

        double axisDeltaY = mousePosition.y - y;

        if (Math.abs(axisDeltaY) > EPSILON && axisBindings.containsKey(MouseAxis.MOUSE_Y)) {
            for (AxisBinding binding : axisBindings.get(MouseAxis.MOUSE_Y)) {
                if (requiredModifierMissing(binding)) continue;
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

            if (buttonBindings.containsKey(mouseButton)) {
                activatePressBindings(mouseButton);
            }
        } else if (action == GLFW_RELEASE) {
            activeButtonTimes.remove(mouseButton);

            if (buttonBindings.containsKey(mouseButton)) {
                activateReleaseBindings(mouseButton);
            }
        }
    }

    private void activateReleaseBindings(InputButton button) {
        for (ButtonBinding binding : buttonBindings.get(button)) {
            activeActions.remove(binding.action());

            if (binding.activationType() == ActivationType.RELEASE
                    && onActionCallback.containsKey(binding.action())
                    && !requiredModifierMissing(binding)) {
                onActionCallback.get(binding.action()).forEach(Runnable::run);
            }
        }
    }

    private void activatePressBindings(InputButton button) {
        for (ButtonBinding binding : buttonBindings.get(button)) {
            if (binding.activationType() != ActivationType.PRESS || requiredModifierMissing(binding)) continue;

            activeActions.add(binding.action());
            if (onActionCallback.containsKey(binding.action())) {
                onActionCallback.get(binding.action()).forEach(Runnable::run);
            }
        }
    }

    private void handleScroll(long win, double xOffset, double yOffset) {
        if (Math.abs(yOffset) > EPSILON && axisBindings.containsKey(MouseAxis.SCROLL_Y)) {
            for (AxisBinding binding : axisBindings.get(MouseAxis.SCROLL_Y)) {
                double axisInput = yOffset * binding.sensitivity() * binding.directionFactor();
                inputAxis.merge(binding.action(), axisInput, Double::sum);
            }
        }

        if (Math.abs(xOffset) > EPSILON && axisBindings.containsKey(MouseAxis.SCROLL_X)) {
            for (AxisBinding binding : axisBindings.get(MouseAxis.SCROLL_X)) {
                double axisInput = xOffset * binding.sensitivity() * binding.directionFactor();
                inputAxis.merge(binding.action(), axisInput, Double::sum);
            }
        }
    }

    private void handleKey(long win, int key, int scancode, int action, int mods) {
        KeyCode keyCode = KeyCode.fromCode(key);
        if (action == GLFW_PRESS) {
            handleKeyPressed(keyCode);
        } else if (action == GLFW_RELEASE) {
            handleKeyReleased(keyCode);
        }
    }

    private void handleKeyReleased(KeyCode keyCode) {
        activeButtonTimes.remove(keyCode);
        if (buttonBindings.containsKey(keyCode)) {
            activateReleaseBindings(keyCode);
        }
    }

    private void handleKeyPressed(KeyCode keyCode) {
        activeButtonTimes.put(keyCode, System.nanoTime() / 1e9);
        if (buttonBindings.containsKey(keyCode)) {
            activatePressBindings(keyCode);
        }
    }

    public void switchContext(InputContext context) {
        activeContext = context;
        buttonBindings.clear();
        axisButtonBindings.clear();
        axisBindings.clear();
        releaseKeys();

        for (Set<InputBinding> bindingSet : bindings.values()) {
            for (InputBinding binding : bindingSet) {
                if (context.containsAction(binding.action())) {
                    activateBinding(binding);
                }
            }
        }
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
            switchContext(activeContext);
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
        switchContext(activeContext);
    }

    private boolean requiredModifierMissing(InputBinding binding) {
        return binding.hasModifier() && !activeButtonTimes.containsKey(binding.modifier());
    }

    public double getAxisActivation(AxisInputAction action) {
        return inputAxis.getOrDefault(action, 0.0);
    }

    public void clearAxisActivations() {
        inputAxis.clear();
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
