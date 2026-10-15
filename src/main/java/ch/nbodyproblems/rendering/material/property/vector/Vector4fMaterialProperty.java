package ch.nbodyproblems.rendering.material.property.vector;

import ch.nbodyproblems.rendering.Color;
import ch.nbodyproblems.rendering.material.property.MaterialProperty;
import org.joml.Vector4f;
import org.lwjgl.system.MemoryStack;

import static org.lwjgl.opengl.GL20.glUniform4f;

public class Vector4fMaterialProperty extends MaterialProperty<Vector4f> {
    public Vector4fMaterialProperty(Vector4f initialValue, String name) {
        super(initialValue, name);
    }

    public Vector4fMaterialProperty(String name) {
        super(new Vector4f(), name);
    }

    @Override
    protected void refreshShaderData(MemoryStack stack) {
        glUniform4f(location, value.x, value.y, value.z, value.w);
    }
}
