package ch.nbodyproblems.rendering.scene.objects;

import ch.nbodyproblems.rendering.material.Material;
import ch.nbodyproblems.rendering.mesh.MeshComponent;
import ch.nbodyproblems.rendering.mesh.primitives.QuadMesh;
import ch.nbodyproblems.rendering.scene.GameObject;

public class Quad extends GameObject {
    public Quad(int divisions, Material material) {
        addComponent(new MeshComponent(new QuadMesh(divisions), material));
    }
}
