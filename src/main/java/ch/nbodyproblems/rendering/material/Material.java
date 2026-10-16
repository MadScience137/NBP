package ch.nbodyproblems.rendering.material;

import ch.nbodyproblems.rendering.material.property.MaterialProperty;
import ch.nbodyproblems.rendering.material.property.matrix.Matrix4fMaterialProperty;
import ch.nbodyproblems.rendering.material.property.vector.Vector2fMaterialProperty;
import ch.nbodyproblems.rendering.material.property.vector.Vector3fMaterialProperty;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryStack;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static ch.nbodyproblems.util.ResourceLoader.loadResource;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL32.GL_GEOMETRY_SHADER;

public abstract class Material {
    public final int shaderProgram;
    public final boolean hasGeometryShader;
    private final Runnable refreshDataTask;

    protected final Matrix4fMaterialProperty transformMatrix = new Matrix4fMaterialProperty("transform");

    protected final Matrix4fMaterialProperty viewProjection = new Matrix4fMaterialProperty("viewProjection");

    protected final Vector3fMaterialProperty cameraPosition = new Vector3fMaterialProperty("cameraPosition");

    protected final Vector2fMaterialProperty viewportSize = new Vector2fMaterialProperty("viewportSize");

    public Material(String vertexSource, String fragmentSource) {
        this(vertexSource, fragmentSource, null);
    }

    public Material(String vertexSource, String fragmentSource, String geometrySource) {
        hasGeometryShader = geometrySource != null;

        int vs = initShader(GL_VERTEX_SHADER, vertexSource);
        int fs = initShader(GL_FRAGMENT_SHADER, fragmentSource);

        if (hasGeometryShader) {
            int gs = initShader(GL_GEOMETRY_SHADER, geometrySource);
            shaderProgram = initShaderProgram(vs, fs, gs);
            glDeleteShader(gs);
        } else {
            shaderProgram = initShaderProgram(vs, fs, 0);
        }

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

    private int initShaderProgram(int vs, int fs, int gs) {
        final int shaderProgram;
        shaderProgram = glCreateProgram();
        glAttachShader(shaderProgram, vs);
        if (gs != 0) glAttachShader(shaderProgram, gs);
        glAttachShader(shaderProgram, fs);
        glLinkProgram(shaderProgram);

        if (glGetProgrami(shaderProgram, GL_LINK_STATUS) == GL_FALSE) {
            throw new IllegalStateException(glGetProgramInfoLog(shaderProgram));
        }

        return shaderProgram;
    }

    private static int initShader(int shaderType, String vertexSource) {
        int shader = glCreateShader(shaderType);
        glShaderSource(shader, loadResource(vertexSource));
        glCompileShader(shader);

        if (glGetShaderi(shader, GL_COMPILE_STATUS) == GL_FALSE) {
            throw new IllegalStateException(vertexSource + ": " + glGetShaderInfoLog(shader));
        }

        return shader;
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

    public void setViewportSize(float width, float height) {
        viewportSize.set(new Vector2f(width, height));
    }
}
