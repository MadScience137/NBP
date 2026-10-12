package ch.nbodyproblems.rendering.material.property.number;

import ch.nbodyproblems.rendering.material.property.MaterialProperty;
import org.lwjgl.system.MemoryStack;

import static org.lwjgl.opengl.GL40.glUniform1d;

public class DoubleMaterialProperty extends MaterialProperty<Double> {
    public DoubleMaterialProperty(Double initialValue, String name) {
        super(initialValue, name);
    }

    public DoubleMaterialProperty(String name) {
        super(0.0, name);
    }

    @Override
    protected void refreshShaderData(MemoryStack stack) {
        glUniform1d(location, value);
    }
}
