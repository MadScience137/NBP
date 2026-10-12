package ch.nbodyproblems.rendering.material.property.texture;

import ch.nbodyproblems.rendering.texture.Texture2D;

public class Texture2DMaterialProperty extends TextureMaterialProperty<Texture2D> {
    public Texture2DMaterialProperty(Texture2D initialValue, int textureUnit, String name) {
        super(initialValue, textureUnit, name);
    }

    public Texture2DMaterialProperty(int textureUnit, String name) {
        this(null, textureUnit, name);
    }
}
