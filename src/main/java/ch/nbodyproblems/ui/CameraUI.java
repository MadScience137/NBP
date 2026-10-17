package ch.nbodyproblems.ui;

import ch.nbodyproblems.rendering.scene.PerspectiveCamera;
import com.spinyowl.legui.component.*;
import com.spinyowl.legui.event.MouseClickEvent;
import com.spinyowl.legui.listener.MouseClickEventListener;
import com.spinyowl.legui.style.Style;
import com.spinyowl.legui.style.flex.FlexStyle.FlexDirection;
import com.spinyowl.legui.style.flex.FlexStyle.JustifyContent;
import org.joml.Vector3dc;
import org.joml.Vector3fc;

import java.util.Locale;

import static com.spinyowl.legui.style.length.LengthType.percent;

/**
 * Compact camera controls with a shared component theme.
 */
public class CameraUI extends UiScene {
    private static final float PANEL_HEIGHT = 240;
    private final Runnable resetCamera;
    private final PerspectiveCamera camera;
    private final Label captureStatus = new Label("Mouse: free");
    private final Label positionLabel = new Label("x: 0 y: 0 z: 0");
    private final Label rotationLabel = new Label("p: 0 y: 0");
    private final Label speedLabel = new Label("Camera speed: 1.0x");
    private final Slider speed = new Slider(1);
    private Panel panel;

    public CameraUI(Runnable resetCamera, PerspectiveCamera camera) {
        this.resetCamera = resetCamera;
        this.camera = camera;
    }

    @Override
    protected void buildLayout() {
        panel = new Panel(20, 20, 370, PANEL_HEIGHT);
        panel.getStyle().setDisplay(Style.DisplayType.FLEX);
        panel.getStyle().setPadding(16);
        panel.getStyle().getFlexStyle().setFlexDirection(FlexDirection.COLUMN);

        Label heading = new Label("Camera");
        flow(heading, 100, 28);
        panel.add(heading);

        Panel info = new Panel();
        flow(info, 100, 28);
        info.getStyle().setMargin(0, 0, 8, 0);
        info.getStyle().setDisplay(Style.DisplayType.FLEX);
        info.getStyle().getFlexStyle().setFlexDirection(FlexDirection.ROW);
        flow(positionLabel, 50, 24);
        flow(rotationLabel, 50, 24);
        info.add(positionLabel);
        info.add(rotationLabel);
        panel.add(info);

        Panel actions = new Panel();
        flow(actions, 100, 38);
        actions.getStyle().setDisplay(Style.DisplayType.FLEX);
        actions.getStyle().setPadding(2);
        actions.getStyle().getFlexStyle().setFlexDirection(FlexDirection.ROW);
        actions.getStyle().getFlexStyle().setJustifyContent(JustifyContent.SPACE_BETWEEN);
        actions.getStyle().setMargin(0, 0, 10, 0);
        actions.add(button("Reset camera", resetCamera));
        actions.add(button("Capture mouse", this::requestViewportFocus));
        panel.add(actions);

        flow(captureStatus, 100, 28);
        flow(speedLabel, 100, 28);
        flow(speed, 100, 26);
        speed.setMinValue(.1f);
        speed.setMaxValue(5);
        speed.setStepSize(.1f);
        speed.addSliderChangeValueEventListener(event -> updateCameraSpeed());
        panel.add(captureStatus);
        panel.add(speedLabel);
        panel.add(speed);

        new CameraTheme().applyAll(panel);
        heading.getStyle().setFontSize(16f);
        frame.getContainer().add(panel);
        updateCameraSpeed();
    }

    @Override
    public void tick(double time, double deltaTime) {
        panel.setSize(Math.clamp(frame.getContainer().getSize().x - 40, 0, 370), PANEL_HEIGHT);
        captureStatus.getTextState().setText("Mouse: " + (inputSystem.isMouseCaptured() ? "captured" : "free"));
        Vector3fc position = camera.getPosition();
        Vector3dc rotation = camera.getRotationDeg();
        positionLabel.getTextState().setText("x: %.2f  y: %.2f  z: %.2f".formatted(position.x(), position.y(), position.z()));
        rotationLabel.getTextState().setText("p: %.2f  y: %.2f  z: %.2f".formatted(rotation.x(), rotation.y(), rotation.z()));
    }

    private void updateCameraSpeed() {
        camera.setCameraSpeed(speed.getValue());
        speedLabel.getTextState().setText(String.format(Locale.ROOT, "Camera speed: %.1fx", speed.getValue()));
    }

    private Button button(String text, Runnable action) {
        Button button = new Button(text);
        flow(button, 48, 34);
        button.getListenerMap().addListener(MouseClickEvent.class, (MouseClickEventListener) event -> {
            if (event.getAction() == MouseClickEvent.MouseClickAction.CLICK) {
                action.run();
            }
        });
        return button;
    }

    private static void flow(Component component, float widthPercent, float height) {
        component.getStyle().setPosition(Style.PositionType.RELATIVE);
        component.getStyle().setWidth(percent(widthPercent));
        component.getStyle().setHeight(height);
        component.getStyle().getFlexStyle().setFlexBasis(Float.NaN);
    }
}
