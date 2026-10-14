package ch.nbodyproblems.rendering.mesh;

import ch.nbodyproblems.rendering.material.Material;
import ch.nbodyproblems.rendering.scene.Component;
import lombok.Getter;
import lombok.Setter;

public class MeshComponent extends Component {
    @Getter
    @Setter
    private TriangleMesh mesh;
    @Getter
    @Setter
    private Material material;

    public MeshComponent(TriangleMesh mesh, Material material) {
        this.mesh = mesh;
        this.material = material;
    }
}
