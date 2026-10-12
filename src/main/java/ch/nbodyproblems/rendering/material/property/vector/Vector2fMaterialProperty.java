package ch.nbodyproblems.rendering.material.property.vector;

import ch.nbodyproblems.rendering.material.property.MaterialProperty;
import org.joml.Vector2f;
import org.lwjgl.system.MemoryStack;

import static org.lwjgl.opengl.GL20.glUniform2f;

public class Vector2fMaterialProperty extends MaterialProperty<Vector2f> {
    public Vector2fMaterialProperty(Vector2f initialValue, String name) {
        super(initialValue, name);
    }

    public Vector2fMaterialProperty(String name) {
        super(new Vector2f(), name);
    }

    @Override
    protected void refreshShaderData(MemoryStack stack) {
        glUniform2f(location, value.x, value.y);
    }
}
