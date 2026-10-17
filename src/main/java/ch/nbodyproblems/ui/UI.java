package ch.nbodyproblems.ui;

import ch.nbodyproblems.input.InputSystem;
import ch.nbodyproblems.rendering.scene.Tickable;
import com.spinyowl.legui.animation.AnimatorProvider;
import com.spinyowl.legui.component.Component;
import com.spinyowl.legui.component.Frame;
import com.spinyowl.legui.event.CursorEnterEvent;
import com.spinyowl.legui.input.Mouse;
import com.spinyowl.legui.listener.processor.EventProcessorProvider;
import com.spinyowl.legui.system.context.CallbackKeeper;
import com.spinyowl.legui.system.context.Context;
import com.spinyowl.legui.system.handler.processor.SystemEventProcessor;
import com.spinyowl.legui.system.layout.LayoutManager;
import com.spinyowl.legui.system.renderer.nvg.NvgRenderer;
import lombok.AccessLevel;
import lombok.Getter;
import org.joml.Vector2f;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.glBindFramebuffer;

/**
 * A small overlay; GUI focus deliberately never suppresses application input.
 */
public final class UI implements Tickable {
    private final SystemEventProcessor events;
    private final NvgRenderer renderer = new NvgRenderer();
    @Getter(AccessLevel.PACKAGE)
    private final Context context;
    private final InputSystem inputSystem;
    private UiScene activeScene;
    private Map<Class<? extends UiScene>, UiScene> scenes = new HashMap<>();
    private Map<Class<? extends UiScene>, Supplier<? extends UiScene>> sceneConstructors = new HashMap<>();
    private Component previousFocus;

    public UI(long window, CallbackKeeper callbacks, InputSystem inputSystem) {
        this.inputSystem = inputSystem;
        events = new EventProcessor(inputSystem);
        SystemEventProcessor.addDefaultCallbacks(callbacks, events);
        context = new Context(window, events);
        renderer.initialize();
    }

    @Override
    public void tick(double time, double deltaTime) {
        context.updateGlfwWindow();

        Frame frame = activeScene.getFrame();
        frame.setSize(context.getWindowSize().x, context.getWindowSize().y);

        if (context.getFocusedGui() != null) {
            previousFocus = context.getFocusedGui();
        }

        events.processEvents(frame, context);

        var focused = context.getFocusedGui();
        boolean isFocused = focused != null && focused != frame.getContainer();
        inputSystem.setGuiContext(isFocused ? activeScene.getInputContext() : null);

        EventProcessorProvider.getInstance().processEvents();
        activeScene.tick(time, deltaTime);
        LayoutManager.getInstance().layout(frame);
        AnimatorProvider.getAnimator().runAnimations();
    }

    public void switchScene(Class<? extends UiScene> clazz) {
        if (!sceneConstructors.containsKey(clazz)) {
            throw new IllegalArgumentException("Ui scene has not been registered.");
        }
        activeScene = scenes.computeIfAbsent(clazz, this::createScene);
    }

    private UiScene createScene(Class<? extends UiScene> clazz) {
        UiScene scene = sceneConstructors.get(clazz).get();
        scene.setInputSystem(inputSystem);
        scene.setUi(this);
        scene.buildLayout();
        return scene;
    }

    public <T extends UiScene> void registerScene(Class<T> clazz, Supplier<T> sceneConstructor) {
        sceneConstructors.put(clazz, sceneConstructor);
    }

    public void render(int width, int height) {
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        glViewport(0, 0, width, height);
        glStencilMask(0xff);
        glClearStencil(0);
        glClear(GL_STENCIL_BUFFER_BIT);
        renderer.render(activeScene.getFrame(), context);
    }

    public void clearInteractionState() {
        Frame frame = activeScene.getFrame();
        var dispatcher = EventProcessorProvider.getInstance();

        for (Mouse.MouseButton button : Mouse.MouseButton.values()) {
            button.setPressed(false);
        }

        var hovered = context.getMouseTargetGui();
        if (hovered != null) {
            Vector2f cursor = new Vector2f(Mouse.getCursorPosition());
            Vector2f local = new Vector2f(cursor).sub(hovered.getAbsolutePosition());

            hovered.setHovered(false);
            hovered.setPressed(false);
            context.setMouseTargetGui(null);

            dispatcher.pushEvent(new CursorEnterEvent<>(hovered, context, frame, false, local, cursor));
        }

        if (previousFocus != null) {
            previousFocus.setPressed(false);
            previousFocus.setFocused(false);
        }

        Context.setFocusedGui(null, context, frame);
        context.setFocusedGui(null);
    }

    public void dispatchPendingEvents() {
        EventProcessorProvider.getInstance().processEvents();
    }

    public void destroy() {
        renderer.destroy();
    }
}
