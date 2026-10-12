package ch.nbodyproblems.rendering.material.property.matrix;

import ch.nbodyproblems.rendering.material.property.MaterialProperty;
import org.joml.Matrix3f;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL20.glUniformMatrix3fv;

public class Matrix3fMaterialProperty extends MaterialProperty<Matrix3f> {
    public Matrix3fMaterialProperty(Matrix3f initialValue, String name) {
        super(initialValue, name);
    }

    public Matrix3fMaterialProperty(String name) {
        super(new Matrix3f(), name);
    }

    @Override
    protected void refreshShaderData(MemoryStack stack) {
        FloatBuffer fb = stack.mallocFloat(16);
        glUniformMatrix3fv(location, false, value.get(fb));
    }
}
