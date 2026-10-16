package ch.nbodyproblems.rendering.mesh;

import org.joml.Vector3fc;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL45.*;

public class PolyLineMesh {
    private static final int POSITION_ATTRIBUTE = 0;
    private static final int VERTEX_BINDING = 0;
    public final int vbo;
    public final int vao;
    public final int pointCount;

    public PolyLineMesh(Vector3fc[] points) {
        pointCount = points.length;
        float[] vertices = new float[points.length * 3];

        for (int i = 0; i < points.length; i++) {
            vertices[i * 3] = points[i].x();
            vertices[i * 3 + 1] = points[i].y();
            vertices[i * 3 + 2] = points[i].z();
        }

        vao = glCreateVertexArrays();
        vbo = glCreateBuffers();

        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer verticesBuffer = stack.mallocFloat(vertices.length);
            verticesBuffer.put(vertices).flip();
            glNamedBufferData(vbo, verticesBuffer, GL_DYNAMIC_DRAW);
        }

        glVertexArrayVertexBuffer(vao, VERTEX_BINDING, vbo, 0, 3 * Float.BYTES);

        glVertexArrayAttribFormat(vao, POSITION_ATTRIBUTE, 3, GL_FLOAT, false, 0);
        glVertexArrayAttribBinding(vao, POSITION_ATTRIBUTE, VERTEX_BINDING);
        glEnableVertexArrayAttrib(vao, POSITION_ATTRIBUTE);
    }
}
