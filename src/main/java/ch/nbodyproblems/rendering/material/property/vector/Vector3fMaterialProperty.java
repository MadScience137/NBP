package ch.nbodyproblems.rendering.material.property.vector;

import ch.nbodyproblems.rendering.material.property.MaterialProperty;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryStack;

import static org.lwjgl.opengl.GL20.glUniform3f;

public class Vector3fMaterialProperty extends MaterialProperty<Vector3f> {
    public Vector3fMaterialProperty(Vector3f initialValue, String name) {
        super(initialValue, name);
    }

    public Vector3fMaterialProperty(String name) {
        super(new Vector3f(), name);
    }

    @Override
    protected void refreshShaderData(MemoryStack stack) {
        glUniform3f(location, value.x, value.y, value.z);
    }
}
