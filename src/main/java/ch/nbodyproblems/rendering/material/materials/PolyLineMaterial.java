package ch.nbodyproblems.rendering.material.materials;

import ch.nbodyproblems.rendering.Color;
import ch.nbodyproblems.rendering.material.Material;
import ch.nbodyproblems.rendering.material.property.number.FloatMaterialProperty;
import ch.nbodyproblems.rendering.material.property.vector.ColorMaterialProperty;
import ch.nbodyproblems.rendering.material.property.vector.Vector2fMaterialProperty;
import org.joml.Vector2f;

public class PolyLineMaterial extends Material {
    private static final String VERTEX_SHADER_PATH = "/shaders/polyLine.vert";
    private static final String FRAGMENT_SHADER_PATH = "/shaders/polyLine.frag";
    private static final String GEOMETRY_SHADER_PATH = "/shaders/polyLine.geom";

    private final ColorMaterialProperty color = new ColorMaterialProperty("color");
    private final FloatMaterialProperty thickness = new FloatMaterialProperty("thickness");

    public PolyLineMaterial(Color color, float thickness) {
        super(VERTEX_SHADER_PATH, FRAGMENT_SHADER_PATH, GEOMETRY_SHADER_PATH);
        setColor(color);
        setThickness(thickness);
    }

    public void setColor(Color color) {
        this.color.set(color);
    }

    public void setThickness(float thickness) {
        this.thickness.set(thickness);
    }
}
