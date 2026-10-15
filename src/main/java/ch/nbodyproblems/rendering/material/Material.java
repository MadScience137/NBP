package ch.nbodyproblems.rendering.material;

import ch.nbodyproblems.rendering.material.property.MaterialProperty;
import ch.nbodyproblems.rendering.material.property.matrix.Matrix4fMaterialProperty;
import ch.nbodyproblems.rendering.material.property.vector.Vector3fMaterialProperty;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryStack;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static ch.nbodyproblems.util.ResourceLoader.loadResource;
import static org.lwjgl.opengl.GL20.*;

public abstract class Material {
    public final int shaderProgram;
    private final Runnable refreshDataTask;

    protected Matrix4fMaterialProperty transformMatrix = new Matrix4fMaterialProperty("transform");

    protected Matrix4fMaterialProperty viewProjection = new Matrix4fMaterialProperty("viewProjection");

    protected Vector3fMaterialProperty cameraPosition = new Vector3fMaterialProperty("cameraPosition");

    public Material(String vertexSource, String fragmentSource) {
        int vs = initShader(GL_VERTEX_SHADER, vertexSource);
        int fs = initShader(GL_FRAGMENT_SHADER, fragmentSource);

        shaderProgram = initShaderProgram(vs, fs);

        glDeleteShader(vs);
        glDeleteShader(fs);

        refreshDataTask = createDataRefreshTask();
    }

    private Runnable createDataRefreshTask() {
        Field[] fields = findMaterialPropertyFields();

        return () -> {
            try (MemoryStack stack = MemoryStack.stackPush()) {
                for (Field field : fields) {
                    try {
                        ((MaterialProperty<?>) field.get(this)).updateShaderData(stack, shaderProgram);
                    } catch (IllegalAccessException e) {
                        throw new IllegalStateException(e);
                    }
                }
            }
        };
    }

    private Field[] findMaterialPropertyFields() {
        List<Field> fields = new ArrayList<>();

        for (Class<?> type = getClass(); type != null && type != Object.class; type = type.getSuperclass()) {
            fields.addAll(
                    Arrays.stream(type.getDeclaredFields())
                            .filter(f -> !Modifier.isStatic(f.getModifiers()))
                            .filter(f -> !f.isSynthetic())
                            .filter(f -> MaterialProperty.class.isAssignableFrom(f.getType()))
                            .filter(Field::trySetAccessible)
                            .toList()
            );
        }

        return fields.toArray(Field[]::new);
    }

    private int initShaderProgram(int vs, int fs) {
        final int shaderProgram;
        shaderProgram = glCreateProgram();
        glAttachShader(shaderProgram, vs);
        glAttachShader(shaderProgram, fs);
        glLinkProgram(shaderProgram);
        return shaderProgram;
    }

    private static int initShader(int glVertexShader, String vertexSource) {
        int vs = glCreateShader(glVertexShader);
        glShaderSource(vs, loadResource(vertexSource));
        glCompileShader(vs);
        return vs;
    }

    public final void updateShaderData() {
        refreshDataTask.run();
    }

    public void setTransformMatrix(Matrix4f objectTransform) {
        transformMatrix.set(objectTransform);
    }

    public void setViewProjectionMatrix(Matrix4f viewProjection) {
        this.viewProjection.set(viewProjection);
    }

    public void setCameraPosition(Vector3f position) {
        cameraPosition.set(position);
    }
}
