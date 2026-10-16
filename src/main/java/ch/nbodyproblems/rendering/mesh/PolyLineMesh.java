package ch.nbodyproblems.rendering.mesh;

import org.joml.Vector3fc;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL45.*;

public class PolyLineMesh {
    private static final int POSITION_ATTRIBUTE = 0;
    private static final int LENGTH_ATTRIBUTE = 1;
    private static final int VERTEX_BINDING = 0;
    public final int vbo;
    public final int vao;
    public final int pointCount;
    public final float lineLength;

    public PolyLineMesh(Vector3fc[] points) {
        pointCount = points.length;
        int floatsPerVert = 4;
        float[] vertices = new float[points.length * floatsPerVert];
        float length = 0;

        for (int i = 0; i < points.length; i++) {
            if (i > 0) length += points[i].distance(points[i - 1]);
            vertices[i * floatsPerVert] = points[i].x();
            vertices[i * floatsPerVert + 1] = points[i].y();
            vertices[i * floatsPerVert + 2] = points[i].z();
            vertices[i * floatsPerVert + 3] = length;
        }

        this.lineLength = length;

        vao = glCreateVertexArrays();
        vbo = glCreateBuffers();

        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer verticesBuffer = stack.mallocFloat(vertices.length);
            verticesBuffer.put(vertices).flip();
            glNamedBufferData(vbo, verticesBuffer, GL_DYNAMIC_DRAW);
        }

        glVertexArrayVertexBuffer(vao, VERTEX_BINDING, vbo, 0, floatsPerVert * Float.BYTES);

        glVertexArrayAttribFormat(vao, POSITION_ATTRIBUTE, 3, GL_FLOAT, false, 0);
        glVertexArrayAttribBinding(vao, POSITION_ATTRIBUTE, VERTEX_BINDING);
        glEnableVertexArrayAttrib(vao, POSITION_ATTRIBUTE);

        glVertexArrayAttribFormat(vao, LENGTH_ATTRIBUTE, 1, GL_FLOAT, false, 3 * Float.BYTES);
        glVertexArrayAttribBinding(vao, LENGTH_ATTRIBUTE, VERTEX_BINDING);
        glEnableVertexArrayAttrib(vao, LENGTH_ATTRIBUTE);
    }
}
