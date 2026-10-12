package ch.nbodyproblems.rendering.mesh;

import ch.nbodyproblems.rendering.material.MaterialBase;
import ch.nbodyproblems.rendering.scene.Component;
import lombok.Getter;
import lombok.Setter;

public class MeshComponent extends Component {
    @Getter
    @Setter
    private TriangleMesh mesh;
    @Getter
    @Setter
    private MaterialBase material;

    public MeshComponent(TriangleMesh mesh, MaterialBase material) {
        this.mesh = mesh;
        this.material = material;
    }
}
