package ch.nbodyproblems.rendering.mesh.primitives;

import ch.nbodyproblems.rendering.material.UnlitMaterial;
import ch.nbodyproblems.rendering.mesh.MeshComponent;
import ch.nbodyproblems.rendering.mesh.TriangleMesh;
import ch.nbodyproblems.rendering.scene.GameObject;

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

    public Quad(float r, float g, float b, float a) {
        TriangleMesh mesh = new TriangleMesh(vertices, triangles);
        MeshComponent meshComponent = new MeshComponent(mesh, new UnlitMaterial(r, g, b, a));
        addComponent(meshComponent);
    }
}
