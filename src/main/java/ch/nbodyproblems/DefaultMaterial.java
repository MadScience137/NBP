package ch.nbodyproblems;

import org.lwjgl.system.MemoryStack;

public class DefaultMaterial extends MaterialBase {
    private static final String VERTEX_SHADER_PATH = "/shaders/triangle.vert";
    private static final String FRAGMENT_SHADER_PATH = "/shaders/triangle.frag";

    public DefaultMaterial() {
        super(VERTEX_SHADER_PATH, FRAGMENT_SHADER_PATH);
    }

    @Override
    protected void refreshShaderData(MemoryStack stack) {
    }
}
