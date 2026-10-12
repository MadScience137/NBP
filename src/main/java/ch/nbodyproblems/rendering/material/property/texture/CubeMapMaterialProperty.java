package ch.nbodyproblems.rendering.material.property.texture;

import ch.nbodyproblems.rendering.texture.CubeMap;

public class CubeMapMaterialProperty extends TextureMaterialProperty<CubeMap> {
    public CubeMapMaterialProperty(CubeMap initialValue, int textureUnit, String name) {
        super(initialValue, textureUnit, name);
    }

    public CubeMapMaterialProperty(int textureUnit, String name) {
        this(null, textureUnit, name);
    }
}
