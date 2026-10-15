package ch.nbodyproblems.rendering.material.property.vector;

import ch.nbodyproblems.rendering.Color;
import ch.nbodyproblems.rendering.material.property.MaterialProperty;
import org.lwjgl.system.MemoryStack;

import static org.lwjgl.opengl.GL20.glUniform4f;

public class ColorMaterialProperty extends MaterialProperty<Color> {
    public ColorMaterialProperty(Color initialValue, String name) {
        super(initialValue, name);
    }

    public ColorMaterialProperty(String name) {
        super(new Color(171, 15, 255), name);
    }

    @Override
    protected void refreshShaderData(MemoryStack stack) {
        glUniform4f(location, value.r(), value.g(), value.b(), value.a());
    }
}
