package ch.nbodyproblems.rendering.scene.objects;

import ch.nbodyproblems.rendering.Color;
import ch.nbodyproblems.rendering.material.materials.PolyLineMaterial;
import ch.nbodyproblems.rendering.mesh.PolyLineComponent;
import ch.nbodyproblems.rendering.mesh.PolyLineMesh;
import ch.nbodyproblems.rendering.scene.GameObject;
import org.joml.Vector3fc;

public class PolyLine extends GameObject {
    public PolyLine(Vector3fc[] points, float thickness, Color color) {
        this(points, thickness, thickness, color);
    }

    public PolyLine(Vector3fc[] points, float thickness, Color startColor, Color endColor) {
        this(points, thickness, thickness, startColor, endColor);
    }

    public PolyLine(Vector3fc[] points, float startThickness, float endThickness, Color color) {
        this(points, startThickness, endThickness, color, color);
    }

    public PolyLine(Vector3fc[] points, float startThickness, float endThickness, Color startColor, Color endColor) {
        PolyLineMesh mesh = new PolyLineMesh(points);
        addComponent(new PolyLineComponent(mesh, new PolyLineMaterial(startColor, endColor, startThickness, endThickness, mesh.lineLength)));
    }
}
