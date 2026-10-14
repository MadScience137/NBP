package ch.nbodyproblems.rendering.scene.objects;

import ch.nbodyproblems.rendering.material.Material;
import ch.nbodyproblems.rendering.mesh.MeshComponent;
import ch.nbodyproblems.rendering.mesh.primitives.CubeMesh;
import ch.nbodyproblems.rendering.mesh.primitives.QuadMesh;
import ch.nbodyproblems.rendering.scene.GameObject;

public class Cube extends GameObject {
    public Cube(int divisions, Material material) {
        addComponent(new MeshComponent(new CubeMesh(divisions), material));
    }
}
