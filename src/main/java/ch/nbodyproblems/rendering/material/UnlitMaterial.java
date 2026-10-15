package ch.nbodyproblems.rendering.material;

import ch.nbodyproblems.rendering.Color;
import ch.nbodyproblems.rendering.material.property.vector.ColorMaterialProperty;
import ch.nbodyproblems.rendering.material.property.vector.Vector4fMaterialProperty;
import org.joml.Vector4f;

public class UnlitMaterial extends Material {
    private static final String VERTEX_SHADER_PATH = "/shaders/unlit.vert";
    private static final String FRAGMENT_SHADER_PATH = "/shaders/unlit.frag";
    private final ColorMaterialProperty color = new ColorMaterialProperty( "color");

    public UnlitMaterial(Color color) {
        super(VERTEX_SHADER_PATH, FRAGMENT_SHADER_PATH);
        setColor(color);
    }

    public void setColor(Color color) {
        this.color.set(color);
    }
}
