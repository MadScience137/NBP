package ch.nbodyproblems.ui;

import ch.nbodyproblems.input.InputContext;
import ch.nbodyproblems.input.InputSystem;
import ch.nbodyproblems.rendering.scene.Tickable;
import com.spinyowl.legui.component.Frame;
import com.spinyowl.legui.style.color.ColorConstants;
import com.spinyowl.legui.system.context.Context;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

public abstract class UiScene implements Tickable {
    @Setter(AccessLevel.PROTECTED)
    protected UI ui;

    @Setter(AccessLevel.PROTECTED)
    protected InputSystem inputSystem;

    @Getter
    protected final Frame frame = new Frame();

    @Getter
    protected InputContext inputContext = InputContext.GLOBAL;


    protected UiScene() {
        frame.getContainer().getStyle().getBackground().setColor(ColorConstants.transparent());
        frame.getContainer().setFocusable(false);
    }

    protected abstract void buildLayout();

    protected void requestViewportFocus() {
        ui.clearInteractionState();
        inputSystem.setMouseCaptured(true);
    }
}
