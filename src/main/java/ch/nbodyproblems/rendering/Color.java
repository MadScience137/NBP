package ch.nbodyproblems.rendering;

import java.util.Objects;

public record Color(float r, float g, float b, float a) {
    public static final Color CRIMSON = new Color(220, 20, 60);
    public static final Color RED = new Color(255, 0, 0);
    public static final Color CORAL = new Color(255, 127, 80);
    public static final Color DARK_ORANGE = new Color(255, 140, 0);
    public static final Color ORANGE = new Color(255, 165, 0);
    public static final Color GOLD = new Color(255, 215, 0);
    public static final Color YELLOW = new Color(255, 255, 0);
    public static final Color OLIVE = new Color(128, 128, 0);
    public static final Color CHARTREUSE = new Color(127, 255, 0);
    public static final Color LIME = new Color(0, 255, 0);
    public static final Color GREEN = new Color(0, 128, 0);
    public static final Color SPRING_GREEN = new Color(0, 255, 127);
    public static final Color TURQUOISE = new Color(64, 224, 208);
    public static final Color CYAN = new Color(0, 255, 255);
    public static final Color TEAL = new Color(0, 128, 128);
    public static final Color SKY_BLUE = new Color(135, 206, 235);
    public static final Color DODGER_BLUE = new Color(30, 144, 255);
    public static final Color BLUE = new Color(0, 0, 255);
    public static final Color NAVY = new Color(0, 0, 128);
    public static final Color INDIGO = new Color(75, 0, 130);
    public static final Color VIOLET = new Color(238, 130, 238);
    public static final Color PURPLE = new Color(128, 0, 128);
    public static final Color MAGENTA = new Color(255, 0, 255);
    public static final Color HOT_PINK = new Color(255, 105, 180);

    public static final Color BROWN = new Color(165, 42, 42);
    public static final Color CHOCOLATE = new Color(210, 105, 30);
    public static final Color TAN = new Color(210, 180, 140);
    public static final Color BEIGE = new Color(245, 245, 220);

    public static final Color BLACK = new Color(0, 0, 0);
    public static final Color DARK_GRAY = new Color(64, 64, 64);
    public static final Color LIGHT_GRAY = new Color(192, 192, 192);
    public static final Color WHITE = new Color(255, 255, 255);

    public Color {
        requireUnit(r, "red");
        requireUnit(g, "green");
        requireUnit(b, "blue");
        requireUnit(a, "alpha");
    }

    public Color(float r, float g, float b) {
        this(r, g, b, 1);
    }

    public Color(int r, int g, int b, float a) {
        this(r / 255f, g / 255f, b / 255f, a);
    }

    public Color(int r, int g, int b) {
        this(r, g, b, 1);
    }

    public static Color rgba(float r, float g, float b, float a) {
        return new Color(r, g, b, a);
    }

    public static Color hsba(float hue, float saturation, float brightness, float alpha) {
        if (!Float.isFinite(hue)) throw new IllegalArgumentException("hue must be finite");
        requireUnit(saturation, "saturation");
        requireUnit(brightness, "brightness");
        requireUnit(alpha, "alpha");

        float sector = (float) (hue - Math.floor(hue)) * 6;
        int index = (int) sector;
        float fraction = sector - index;
        float p = brightness * (1 - saturation);
        float q = brightness * (1 - saturation * fraction);
        float t = brightness * (1 - saturation * (1 - fraction));
        return switch (index % 6) {
            case 0 -> rgba(brightness, t, p, alpha);
            case 1 -> rgba(q, brightness, p, alpha);
            case 2 -> rgba(p, brightness, t, alpha);
            case 3 -> rgba(p, q, brightness, alpha);
            case 4 -> rgba(t, p, brightness, alpha);
            default -> rgba(brightness, p, q, alpha);
        };
    }

    public float hue() {
        float max = brightness();
        float delta = max - Math.min(r, Math.min(g, b));
        if (delta == 0) return 0;
        float hue;
        if (max == r) hue = (g - b) / delta;
        else if (max == g) hue = (b - r) / delta + 2;
        else hue = (r - g) / delta + 4;
        hue /= 6;
        return hue < 0 ? hue + 1 : hue;
    }

    public float saturation() {
        float max = brightness();
        return max == 0 ? 0 : (max - Math.min(r, Math.min(g, b))) / max;
    }

    public float brightness() {
        return Math.max(r, Math.max(g, b));
    }

    public Color derive(float hueFactor, float saturationFactor, float brightnessFactor, float alphaFactor) {
        requireFactor(hueFactor, "hueFactor");
        requireFactor(saturationFactor, "saturationFactor");
        requireFactor(brightnessFactor, "brightnessFactor");
        requireFactor(brightnessFactor, "alphaFactor");
        return hsba(
                hue() * hueFactor,
                Math.min(1, saturation() * saturationFactor),
                Math.min(1, brightness() * brightnessFactor),
                Math.clamp(a * alphaFactor, 0, 1)
        );
    }

    public Color mix(Color other, float factor) {
        Objects.requireNonNull(other, "other");
        requireUnit(factor, "factor");
        if (factor == 0) return this;
        if (factor == 1) return other;
        return rgba(
                r + (other.r - r) * factor,
                g + (other.g - g) * factor,
                b + (other.b - b) * factor,
                a + (other.a - a) * factor
        );
    }

    private static void requireUnit(float value, String name) {
        if (!Float.isFinite(value) || value < 0 || value > 1) {
            throw new IllegalArgumentException(name + " must be finite and between 0 and 1");
        }
    }

    private static void requireFactor(float value, String name) {
        if (!Float.isFinite(value) || value < 0) {
            throw new IllegalArgumentException(name + " must be finite and nonnegative");
        }
    }
}
