package ch.nbodyproblems;

public class Quad extends GameObject {
    private static final float[] vertices = {
            -1f, 1f, 0f,
            -1f, -1f, 0f,
            1f, -1f, 0f,
            1f, 1f, 0f
    };

    private static final int[] triangles = {
            0, 1, 2,
            2, 3, 0
    };

    public Quad() {
        TriangleMesh mesh = new TriangleMesh(vertices, triangles);
        MeshComponent meshComponent = new MeshComponent(mesh, new DefaultMaterial());
        addComponent(meshComponent);
    }
}
