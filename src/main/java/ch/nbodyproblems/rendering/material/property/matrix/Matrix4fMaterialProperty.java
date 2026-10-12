package ch.nbodyproblems.rendering.material.property.matrix;

import ch.nbodyproblems.rendering.material.property.MaterialProperty;
import org.joml.Matrix4f;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;

public class Matrix4fMaterialProperty extends MaterialProperty<Matrix4f> {
    public Matrix4fMaterialProperty(Matrix4f initialValue, String name) {
        super(initialValue, name);
    }

    public Matrix4fMaterialProperty(String name) {
        super(new Matrix4f(), name);
    }

    @Override
    protected void refreshShaderData(MemoryStack stack) {
        FloatBuffer fb = stack.mallocFloat(16);
        glUniformMatrix4fv(location, false, value.get(fb));
    }
}
