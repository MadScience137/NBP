package ch.nbodyproblems.rendering.scene;

import lombok.Getter;
import lombok.Setter;

public abstract class Component {
    @Getter
    @Setter
    private boolean enabled = true;
}
