package ch.nbodyproblems.rendering.material;

import org.joml.Vector4f;
import org.lwjgl.system.MemoryStack;

import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glUniform4f;

public class UnlitMaterial extends MaterialBase {
    private static final String VERTEX_SHADER_PATH = "/shaders/triangle.vert";
    private static final String FRAGMENT_SHADER_PATH = "/shaders/triangle.frag";
    private Vector4f color;
    private final int colorLoc;

    public UnlitMaterial(float r, float g, float b, float a) {
        super(VERTEX_SHADER_PATH, FRAGMENT_SHADER_PATH);
        setColor(r, g, b, a);
        colorLoc = glGetUniformLocation(shaderProgram, "color");
    }

    public void setColor(float r, float g, float b, float a) {
        setColor(new Vector4f(r, g, b, a));
    }

    public void setColor(Vector4f color) {
        this.color = color;
    }

    @Override
    protected void refreshShaderData(MemoryStack stack) {
        glUniform4f(colorLoc, color.x, color.y, color.z, color.w);
    }
}
