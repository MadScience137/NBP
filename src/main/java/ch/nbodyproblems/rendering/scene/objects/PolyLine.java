package ch.nbodyproblems.rendering.scene.objects;

import ch.nbodyproblems.rendering.Color;
import ch.nbodyproblems.rendering.material.materials.PolyLineMaterial;
import ch.nbodyproblems.rendering.mesh.PolyLineComponent;
import ch.nbodyproblems.rendering.mesh.PolyLineMesh;
import ch.nbodyproblems.rendering.scene.GameObject;
import org.joml.Vector3fc;

public class PolyLine extends GameObject {
    public PolyLine(Vector3fc[] points, float thickness, Color color) {
        addComponent(new PolyLineComponent(new PolyLineMesh(points), new PolyLineMaterial(color, thickness)));
    }
}
