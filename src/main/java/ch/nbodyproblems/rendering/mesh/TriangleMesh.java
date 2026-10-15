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
    private static final int NORMAL_ATTRIBUTE = 1;
    private static final int TEX_COORD_ATTRIBUTE = 2;

    public final int elementCount;
    public final int vbo;
    public final int vao;
    public final int ebo;
    public final CullFace cullFace;

    public TriangleMesh(float[] vertices, int[] triangles, float[] normals, CullFace cullFace) {
        this(vertices, triangles, normals, null, false, cullFace);
    }

    public TriangleMesh(float[] vertices, int[] triangles, float[] normals) {
        this(vertices, triangles, normals, null, false, CullFace.BACK);
    }

    public TriangleMesh(float[] vertices, int[] triangles, float[] normals, float[] texCoordinates, CullFace cullFace) {
        this(vertices, triangles, normals, texCoordinates, true, cullFace);
    }

    public TriangleMesh(float[] vertices, int[] triangles, float[] normals, float[] texCoordinates) {
        if (texCoordinates == null || texCoordinates.length / 2 != vertices.length / 3) {
            throw new IllegalArgumentException("Texture coordinates do not match vertex count");
        }
        this(vertices, triangles, normals, texCoordinates, true, CullFace.BACK);
    }

    private TriangleMesh(float[] vertices, int[] triangles, float[] normals, float[] texCoordinates, boolean hasTexture, CullFace cullFace) {
        this.cullFace = cullFace;
        elementCount = triangles.length;

        float[] vertexData;
        int floatsPerVert;

        if (hasTexture) {
            vertexData = new float[vertices.length + normals.length + texCoordinates.length];
            floatsPerVert = 8;

            for (int i = 0; i < vertices.length / 3; i++) {
                vertexData[i * floatsPerVert] = vertices[i * 3];
                vertexData[i * floatsPerVert + 1] = vertices[i * 3 + 1];
                vertexData[i * floatsPerVert + 2] = vertices[i * 3 + 2];
                vertexData[i * floatsPerVert + 3] = normals[i * 3];
                vertexData[i * floatsPerVert + 4] = normals[i * 3 + 1];
                vertexData[i * floatsPerVert + 5] = normals[i * 3 + 2];
                vertexData[i * floatsPerVert + 6] = texCoordinates[i * 2];
                vertexData[i * floatsPerVert + 7] = texCoordinates[i * 2 + 1];
            }
        } else {
            vertexData = new float[vertices.length + normals.length];
            floatsPerVert = 6;

            for (int i = 0; i < vertices.length / 3; i++) {
                vertexData[i * floatsPerVert] = vertices[i * 3];
                vertexData[i * floatsPerVert + 1] = vertices[i * 3 + 1];
                vertexData[i * floatsPerVert + 2] = vertices[i * 3 + 2];
                vertexData[i * floatsPerVert + 3] = normals[i * 3];
                vertexData[i * floatsPerVert + 4] = normals[i * 3 + 1];
                vertexData[i * floatsPerVert + 5] = normals[i * 3 + 2];
            }
        }

        vao = glCreateVertexArrays();
        vbo = glCreateBuffers();
        ebo = glCreateBuffers();

        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer verticesBuffer = stack.mallocFloat(vertexData.length);
            verticesBuffer.put(vertexData).flip();
            glNamedBufferData(vbo, verticesBuffer, GL_STATIC_DRAW);

            IntBuffer indexBuffer = stack.mallocInt(triangles.length);
            indexBuffer.put(triangles).flip();
            glNamedBufferData(ebo, indexBuffer, GL_STATIC_DRAW);
        }

        glVertexArrayElementBuffer(vao, ebo);
        glVertexArrayVertexBuffer(vao, VERTEX_BINDING, vbo, 0, floatsPerVert * Float.BYTES);

        glVertexArrayAttribFormat(vao, POSITION_ATTRIBUTE, 3, GL_FLOAT, false, 0);
        glVertexArrayAttribBinding(vao, POSITION_ATTRIBUTE, VERTEX_BINDING);
        glEnableVertexArrayAttrib(vao, POSITION_ATTRIBUTE);

        glVertexArrayAttribFormat(vao, NORMAL_ATTRIBUTE, 3, GL_FLOAT, false, 3 * Float.BYTES);
        glVertexArrayAttribBinding(vao, NORMAL_ATTRIBUTE, VERTEX_BINDING);
        glEnableVertexArrayAttrib(vao, NORMAL_ATTRIBUTE);

        if (hasTexture) {
            glVertexArrayAttribFormat(vao, TEX_COORD_ATTRIBUTE, 2, GL_FLOAT, false, 6 * Float.BYTES);
            glVertexArrayAttribBinding(vao, TEX_COORD_ATTRIBUTE, VERTEX_BINDING);
            glEnableVertexArrayAttrib(vao, TEX_COORD_ATTRIBUTE);
        }
    }
}
