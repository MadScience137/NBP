package ch.nbodyproblems;

import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL45.*;

public class TriangleMesh {
    private static final int POSITION_ATTRIBUTE = 0;
    private static final int VERTEX_BINDING = 0;

    public final int elementCount;
    public final int vbo;
    public final int vao;
    public final int ebo;

    public TriangleMesh(float[] vertices, int[] triangles) {
        vao = glCreateVertexArrays();
        vbo = glCreateBuffers();
        ebo = glCreateBuffers();
        elementCount = triangles.length / 3;

        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer verticesBuffer = stack.mallocFloat(vertices.length);
            verticesBuffer.put(vertices).flip();
            glNamedBufferData(vbo, vertices, GL_STATIC_DRAW);

            IntBuffer indexBuffer = stack.mallocInt(triangles.length);
            indexBuffer.put(triangles).flip();
            glNamedBufferData(ebo, indexBuffer, GL_STATIC_DRAW);
        }

        glVertexArrayVertexBuffer(vao, VERTEX_BINDING, vbo, 0, 3 * Float.BYTES);
        glVertexArrayAttribFormat(vao, POSITION_ATTRIBUTE, 3, GL_FLOAT, false, 0);
        glVertexArrayAttribBinding(vao, POSITION_ATTRIBUTE, VERTEX_BINDING);
        glEnableVertexArrayAttrib(vao, POSITION_ATTRIBUTE);
        glVertexArrayElementBuffer(vao, ebo);
    }
}
