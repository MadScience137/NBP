package ch.nbodyproblems.rendering.scene.objects;

import ch.nbodyproblems.rendering.material.Material;
import ch.nbodyproblems.rendering.mesh.MeshComponent;
import ch.nbodyproblems.rendering.mesh.primitives.CubeMesh;
import ch.nbodyproblems.rendering.mesh.primitives.CubeSphereMesh;
import ch.nbodyproblems.rendering.scene.GameObject;

public class CubeSphere extends GameObject {
    public CubeSphere(int divisions, Material material) {
        addComponent(new MeshComponent(new CubeSphereMesh(divisions), material));
    }
}
