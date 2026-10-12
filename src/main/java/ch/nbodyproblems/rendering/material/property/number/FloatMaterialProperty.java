package ch.nbodyproblems.rendering.material.property.number;

import ch.nbodyproblems.rendering.material.property.MaterialProperty;
import org.lwjgl.system.MemoryStack;

import static org.lwjgl.opengl.GL20.glUniform1f;

public class FloatMaterialProperty extends MaterialProperty<Float> {
    public FloatMaterialProperty(Float initialValue, String name) {
        super(initialValue, name);
    }

    public FloatMaterialProperty(String name) {
        super(0.0f, name);
    }

    @Override
    protected void refreshShaderData(MemoryStack stack) {
        glUniform1f(location, value);
    }
}
