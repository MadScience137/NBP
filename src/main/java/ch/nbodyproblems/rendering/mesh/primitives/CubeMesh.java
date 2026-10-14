package ch.nbodyproblems.rendering.mesh.primitives;

import ch.nbodyproblems.rendering.mesh.TriangleMesh;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public class CubeMesh extends TriangleMesh {
    private static final Vector3fc POSITIVE_Z = new Vector3f(0, 0, 1);
    private static final Vector3fc POSITIVE_Y = new Vector3f(0, 1, 1);

    private static final Vector3fc[] faces = {
            new Vector3f(1, 0, 0),
            new Vector3f(-1, 0, 0),
            new Vector3f(0, 1, 0),
            new Vector3f(0, -1, 0),
            new Vector3f(0, 0, 1),
            new Vector3f(0, 0, -1),
    };

    public final int divisions;

    public CubeMesh(int divisions) {
        this.divisions = divisions;
        int resolution = divisions + 1;
        int numVertices = 6 * resolution * resolution;
        int numTriangles = divisions * divisions * faces.length * 2;

        float[] vertices = new float[numVertices * 3];
        float[] texCoords = new float[numVertices * 2];
        int[] triangles = new int[numTriangles * 3];

        for (int i = 0; i < faces.length; i++) {
            createFace(i, divisions, resolution, vertices, triangles, texCoords);
        }

        super(vertices, triangles, texCoords);
    }

    private static void createFace(int faceIndex, int divisions, int resolution,
                            float[] vertices, int[] triangles, float[] texCoords) {
        int vertPerFace = resolution * resolution;
        int quadsPerFace = divisions * divisions;

        Vector3fc up = faces[faceIndex];
        Vector3fc reference = Math.abs(up.z()) > 0 ? POSITIVE_Y : POSITIVE_Z;
        Vector3fc right = up.cross(reference, new Vector3f()).normalize();
        Vector3fc forward = right.cross(up, new Vector3f()).normalize();

        float texXOffset = (faceIndex % 3) / 3f;
        float texYOffset = (faceIndex / 3) / 2f;

        for (int uStep = 0; uStep < resolution; uStep++) {
            for (int vStep = 0; vStep < resolution; vStep++) {
                int vertPos = uStep * resolution + vStep + vertPerFace * faceIndex;
                float u = (float) uStep / divisions - 0.5f;
                float v = (float) vStep / divisions - 0.5f;

                float x = up.x() * 0.5f + right.x() * u + forward.x() * v;
                float y = up.y() * 0.5f + right.y() * u + forward.y() * v;
                float z = up.z() * 0.5f + right.z() * u + forward.z() * v;

                vertices[vertPos * 3] = x;
                vertices[vertPos * 3 + 1] = y;
                vertices[vertPos * 3 + 2] = z;

                texCoords[vertPos * 2] = texXOffset + u / 3;
                texCoords[vertPos * 2 + 1] = texYOffset + v / 2;

                if (uStep < divisions && vStep < divisions) {
                    int triPos = uStep * divisions + vStep + quadsPerFace * faceIndex;
                    int nextVert = vertPos + 1;
                    int vertAbove = vertPos + resolution;
                    int nextVertAbove = nextVert + resolution;

                    triangles[triPos * 6] = vertPos;
                    triangles[triPos * 6 + 1] = nextVert;
                    triangles[triPos * 6 + 2] = vertAbove;
                    triangles[triPos * 6 + 3] = vertAbove;
                    triangles[triPos * 6 + 4] = nextVert;
                    triangles[triPos * 6 + 5] = nextVertAbove;
                }
            }
        }
    }
}
