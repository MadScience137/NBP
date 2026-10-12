package ch.nbodyproblems.rendering.material;

import ch.nbodyproblems.rendering.material.property.vector.Vector4fMaterialProperty;
import org.joml.Vector4f;

public class UnlitMaterial extends MaterialBase {
    private static final String VERTEX_SHADER_PATH = "/shaders/triangle.vert";
    private static final String FRAGMENT_SHADER_PATH = "/shaders/triangle.frag";
    private final Vector4fMaterialProperty color = new Vector4fMaterialProperty( "color");

    public UnlitMaterial(float r, float g, float b, float a) {
        super(VERTEX_SHADER_PATH, FRAGMENT_SHADER_PATH);
        setColor(r, g, b, a);
    }

    public void setColor(float r, float g, float b, float a) {
        setColor(new Vector4f(r, g, b, a));
    }

    public void setColor(Vector4f color) {
        this.color.set(color);
    }
}
