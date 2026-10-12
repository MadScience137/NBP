package ch.nbodyproblems.rendering.material;

import lombok.Getter;
import lombok.Setter;
import org.joml.Matrix4f;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;

import static ch.nbodyproblems.util.ResourceLoader.loadResource;
import static org.lwjgl.opengl.GL20.*;

public abstract class MaterialBase {
    public final int shaderProgram;
    private final int projLoc;
    private final int transformLoc;

    @Getter
    @Setter
    protected Matrix4f transformMatrix = new Matrix4f();
    @Getter
    @Setter
    protected Matrix4f projectionMatrix = new Matrix4f();

    public MaterialBase(String vertexSource, String fragmentSource) {
        int vs = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(vs, loadResource(vertexSource));
        glCompileShader(vs);

        int fs = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(fs, loadResource(fragmentSource));
        glCompileShader(fs);

        shaderProgram = glCreateProgram();
        glAttachShader(shaderProgram, vs);
        glAttachShader(shaderProgram, fs);
        glLinkProgram(shaderProgram);

        glDeleteShader(vs);
        glDeleteShader(fs);

        projLoc = glGetUniformLocation(shaderProgram, "projection");
        transformLoc = glGetUniformLocation(shaderProgram, "transform");
    }

    public final void updateShaderData() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer fb = stack.mallocFloat(16);
            glUniformMatrix4fv(projLoc, false, projectionMatrix.get(fb));
            glUniformMatrix4fv(transformLoc, false, transformMatrix.get(fb));
            refreshShaderData(stack);
        }
    }

    protected abstract void refreshShaderData(MemoryStack stack);
}
