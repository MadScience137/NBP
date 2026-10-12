package ch.nbodyproblems.rendering.material.property.number;

import ch.nbodyproblems.rendering.material.property.MaterialProperty;
import org.lwjgl.system.MemoryStack;

import static org.lwjgl.opengl.GL20.glUniform1i;

public class IntMaterialProperty extends MaterialProperty<Integer> {
    public IntMaterialProperty(Integer initialValue, String name) {
        super(initialValue, name);
    }

    public IntMaterialProperty(String name) {
        super(0, name);
    }

    @Override
    protected void refreshShaderData(MemoryStack stack) {
        glUniform1i(location, value);
    }
}
