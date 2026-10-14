package ch.nbodyproblems.rendering.mesh.primitives;

import ch.nbodyproblems.rendering.mesh.CullFace;
import ch.nbodyproblems.rendering.mesh.TriangleMesh;

public class QuadMesh extends TriangleMesh {
    public final int divisions;

    public QuadMesh(int divisions) {
        this.divisions = divisions;
        int vertPerSide = divisions + 1;
        int numTriangles = divisions * divisions * 2;

        float[] vertices = new float[vertPerSide * vertPerSide * 3];
        float[] normals = new float[vertPerSide * vertPerSide * 3];
        float[] texCoords = new float[vertPerSide * vertPerSide * 2];
        int[] triangles = new int[numTriangles * 3];

        for (int x = 0; x < vertPerSide; x++) {
            for (int y = 0; y < vertPerSide; y++) {
                int vertPos = x * vertPerSide + y;
                float xVal = (float) x / divisions - 0.5f;
                float yVal = (float) y / divisions - 0.5f;

                vertices[vertPos * 3] = xVal;
                vertices[vertPos * 3 + 1] = yVal;
                vertices[vertPos * 3 + 2] = 0;

                normals[vertPos * 3] = 0;
                normals[vertPos * 3 + 1] = 0;
                normals[vertPos * 3 + 2] = 1;

                texCoords[vertPos * 2] = xVal;
                texCoords[vertPos * 2 + 1] = yVal;

                if (x < divisions && y < divisions) {
                    int triPos = x * divisions + y;
                    int nextVert = vertPos + 1;
                    int vertAbove = vertPos + vertPerSide;
                    int nextVertAbove = nextVert + vertPerSide;

                    triangles[triPos * 6] = vertPos;
                    triangles[triPos * 6 + 1] = vertAbove;
                    triangles[triPos * 6 + 2] = nextVert;
                    triangles[triPos * 6 + 3] = vertAbove;
                    triangles[triPos * 6 + 4] = nextVertAbove;
                    triangles[triPos * 6 + 5] = nextVert;
                }
            }
        }

        super(vertices, triangles, normals, texCoords, CullFace.NONE);
    }
}
