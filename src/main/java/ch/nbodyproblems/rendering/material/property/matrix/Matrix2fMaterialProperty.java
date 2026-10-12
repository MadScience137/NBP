package ch.nbodyproblems.rendering.material.property.matrix;

import ch.nbodyproblems.rendering.material.property.MaterialProperty;
import org.joml.Matrix2f;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL20.glUniformMatrix2fv;

public class Matrix2fMaterialProperty extends MaterialProperty<Matrix2f> {
    public Matrix2fMaterialProperty(Matrix2f initialValue, String name) {
        super(initialValue, name);
    }

    public Matrix2fMaterialProperty(String name) {
        super(new Matrix2f(), name);
    }

    @Override
    protected void refreshShaderData(MemoryStack stack) {
        FloatBuffer fb = stack.mallocFloat(16);
        glUniformMatrix2fv(location, false, value.get(fb));
    }
}
