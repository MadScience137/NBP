package ch.nbodyproblems.rendering.mesh;

import ch.nbodyproblems.rendering.material.Material;
import ch.nbodyproblems.rendering.scene.Component;
import lombok.Getter;

public class PolyLineComponent extends Component {
    @Getter
    private final PolyLineMesh mesh;

    @Getter
    private final Material material;

    public PolyLineComponent(PolyLineMesh mesh, Material material) {
        this.mesh = mesh;
        this.material = material;
    }
}
