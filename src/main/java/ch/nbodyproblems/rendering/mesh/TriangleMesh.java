package ch.nbodyproblems.rendering.mesh;

import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL45.*;

public class TriangleMesh {
    private static final int POSITION_ATTRIBUTE = 0;
    private static final int VERTEX_BINDING = 0;
    private static final int TEX_COORD_ATTRIBUTE = 1;

    public final int elementCount;
    public final int vbo;
    public final int vao;
    public final int ebo;
    public final CullFace cullFace;

    public TriangleMesh(float[] vertices, int[] triangles, CullFace cullFace) {
        this(vertices, triangles, null, false, cullFace);
    }
    public TriangleMesh(float[] vertices, int[] triangles) {
        this(vertices, triangles, null, false, CullFace.BACK);
    }

    public TriangleMesh(float[] vertices, int[] triangles, float[] texCoordinates, CullFace cullFace) {
        this(vertices, triangles, texCoordinates, true, cullFace);
    }
    public TriangleMesh(float[] vertices, int[] triangles, float[] texCoordinates) {
        if (texCoordinates == null || texCoordinates.length / 2 != vertices.length / 3) {
            throw new IllegalArgumentException("Texture coordinates do not match vertex count");
        }
        this(vertices, triangles, texCoordinates, true, CullFace.BACK);
    }

    private TriangleMesh(float[] vertices, int[] triangles, float[] texCoordinates, boolean hasTexture, CullFace cullFace) {
        this.cullFace = cullFace;
        elementCount = triangles.length;

        vao = glCreateVertexArrays();
        vbo = glCreateBuffers();
        ebo = glCreateBuffers();

        float[] vertexData;

        if (hasTexture) {
            vertexData = new float[vertices.length + texCoordinates.length];

            for (int i = 0; i < vertices.length / 3; i++) {
                vertexData[i * 5] = vertices[i * 3];
                vertexData[i * 5 + 1] = vertices[i * 3 + 1];
                vertexData[i * 5 + 2] = vertices[i * 3 + 2];
                vertexData[i * 5 + 3] = texCoordinates[i * 2];
                vertexData[i * 5 + 4] = texCoordinates[i * 2 + 1];
            }
        } else {
            vertexData = vertices;
        }

        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer verticesBuffer = stack.mallocFloat(vertexData.length);
            verticesBuffer.put(vertexData).flip();
            glNamedBufferData(vbo, verticesBuffer, GL_STATIC_DRAW);

            IntBuffer indexBuffer = stack.mallocInt(triangles.length);
            indexBuffer.put(triangles).flip();
            glNamedBufferData(ebo, indexBuffer, GL_STATIC_DRAW);
        }

        int stride = hasTexture ? 5 : 3;
        glVertexArrayVertexBuffer(vao, VERTEX_BINDING, vbo, 0, stride * Float.BYTES);
        glVertexArrayAttribFormat(vao, POSITION_ATTRIBUTE, 3, GL_FLOAT, false, 0);
        glVertexArrayAttribBinding(vao, POSITION_ATTRIBUTE, VERTEX_BINDING);
        glEnableVertexArrayAttrib(vao, POSITION_ATTRIBUTE);
        glVertexArrayElementBuffer(vao, ebo);

        if (hasTexture) {
            glVertexArrayAttribFormat(vao, TEX_COORD_ATTRIBUTE, 2, GL_FLOAT, false, 3 * Float.BYTES);
            glVertexArrayAttribBinding(vao, TEX_COORD_ATTRIBUTE, VERTEX_BINDING);
            glEnableVertexArrayAttrib(vao, TEX_COORD_ATTRIBUTE);
        }
    }
}
