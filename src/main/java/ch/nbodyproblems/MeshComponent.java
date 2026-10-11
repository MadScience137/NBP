package ch.nbodyproblems;

import lombok.Getter;
import lombok.Setter;

public class MeshComponent implements Component {
    @Getter
    @Setter
    private TriangleMesh mesh;
    @Getter
    @Setter
    private MaterialBase material;

    protected MeshComponent(TriangleMesh mesh, MaterialBase material) {
        this.mesh = mesh;
        this.material = material;
    }
}
