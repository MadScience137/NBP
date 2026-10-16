package ch.nbodyproblems.rendering.material.materials;

import ch.nbodyproblems.rendering.Color;
import ch.nbodyproblems.rendering.material.Material;
import ch.nbodyproblems.rendering.material.property.number.FloatMaterialProperty;
import ch.nbodyproblems.rendering.material.property.vector.ColorMaterialProperty;

public class PolyLineMaterial extends Material {
    private static final String VERTEX_SHADER_PATH = "/shaders/polyLine.vert";
    private static final String FRAGMENT_SHADER_PATH = "/shaders/polyLine.frag";
    private static final String GEOMETRY_SHADER_PATH = "/shaders/polyLine.geom";

    private final ColorMaterialProperty startColor = new ColorMaterialProperty("startColor");
    private final ColorMaterialProperty endColor = new ColorMaterialProperty("endColor");
    private final FloatMaterialProperty startThickness = new FloatMaterialProperty("startThickness");
    private final FloatMaterialProperty endThickness = new FloatMaterialProperty("endThickness");
    private final FloatMaterialProperty length = new FloatMaterialProperty("length");

    public PolyLineMaterial(Color color, float thickness, float length) {
        this(color, color, thickness, length);
    }

    public PolyLineMaterial(Color color, float startThickness, float endThickness, float length) {
        this(color, color, startThickness, endThickness, length);
    }

    public PolyLineMaterial(Color startColor, Color endColor, float thickness, float length) {
        this(startColor, endColor, thickness, thickness, length);
    }

    public PolyLineMaterial(Color startColor, Color endColor, float startThickness, float endThickness, float length) {
        super(VERTEX_SHADER_PATH, FRAGMENT_SHADER_PATH, GEOMETRY_SHADER_PATH);
        setStartColor(startColor);
        setEndColor(endColor);
        setStartThickness(startThickness);
        setEndThickness(endThickness);
        this.length.set(length);
    }

    public void setStartColor(Color color) {
        this.startColor.set(color);
    }

    public void setEndColor(Color color) {
        this.endColor.set(color);
    }

    public void setStartThickness(float thickness) {
        this.startThickness.set(thickness);
    }

    public void setEndThickness(float thickness) {
        this.endThickness.set(thickness);
    }
}
