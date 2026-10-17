package ch.nbodyproblems.ui;

import com.spinyowl.legui.component.Button;
import com.spinyowl.legui.component.Component;
import com.spinyowl.legui.component.Label;
import com.spinyowl.legui.component.Panel;
import com.spinyowl.legui.component.Slider;
import com.spinyowl.legui.component.optional.align.HorizontalAlign;
import com.spinyowl.legui.style.Style;
import com.spinyowl.legui.style.border.SimpleLineBorder;
import com.spinyowl.legui.theme.AbstractTheme;
import com.spinyowl.legui.theme.DefaultThemeManager;
import com.spinyowl.legui.theme.Theme;
import org.joml.Vector4f;

import java.util.function.Consumer;

public class CameraTheme extends Theme {
    private static final Vector4f SURFACE = color(23, 30, 45);
    private static final Vector4f TEXT = color(235, 241, 250);
    private static final Vector4f MUTED = color(156, 174, 199);
    private static final Vector4f ACCENT = color(87, 194, 223);
    private static final Vector4f PRESSED = color(170, 143, 244);

    public CameraTheme() {
        super(new DefaultThemeManager());
        register(Panel.class, panel -> surface(panel.getStyle(), SURFACE, 12));
        register(Label.class, label -> {
            surface(label.getStyle(), new Vector4f(0, 0, 0, 0), 0);
            label.setFocusable(false);
            label.getStyle().setFontSize(13f);
            label.getStyle().setTextColor(MUTED);
            label.getStyle().setHorizontalAlign(HorizontalAlign.LEFT);
        });
        register(Button.class, button -> {
            surface(button.getStyle(), SURFACE, 8);
            button.getStyle().setFontSize(13f);
            button.getStyle().setTextColor(TEXT);
            button.getStyle().setBorder(new SimpleLineBorder(MUTED, 1));
            surface(button.getHoveredStyle(), ACCENT, 8);
            button.getHoveredStyle().setTextColor(SURFACE);
            surface(button.getPressedStyle(), PRESSED, 8);
            button.getPressedStyle().setTextColor(SURFACE);
            button.getFocusedStyle().setBorder(new SimpleLineBorder(ACCENT, 2));
        });
        register(Slider.class, slider -> {
            surface(slider.getStyle(), new Vector4f(0, 0, 0, 0), 0);
            slider.setSliderActiveColor(new Vector4f(ACCENT));
            slider.setSliderColor(new Vector4f(MUTED));
            slider.setSliderSize(14);
            slider.getStyle().setFocusedStrokeColor(new Vector4f(ACCENT));
        });
    }

    @Override
    public <T extends Component> void applyAll(T component) {
        apply(component);
        component.getChildComponents().forEach(this::applyAll);
    }

    private <T extends Component> void register(Class<T> type, Consumer<T> styling) {
        getThemeManager().setComponentTheme(type, new AbstractTheme<>() {
            @Override
            public void apply(T component) {
                styling.accept(component);
            }
        });
    }

    private static void surface(Style style, Vector4f background, float radius) {
        style.getBackground().setColor(new Vector4f(background));
        style.setBorder(null);
        style.setBorderRadius(radius);
        style.setShadow(null);
    }

    private static Vector4f color(int r, int g, int b) {
        return new Vector4f(r / 255f, g / 255f, b / 255f, 1);
    }
}
