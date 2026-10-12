package ch.nbodyproblems.rendering.material.property.texture;

import ch.nbodyproblems.rendering.material.property.MaterialProperty;
import ch.nbodyproblems.rendering.texture.Texture;
import org.lwjgl.system.MemoryStack;

import static org.lwjgl.opengl.GL20.glUniform1i;
import static org.lwjgl.opengl.GL45.glBindTextureUnit;

public abstract class TextureMaterialProperty<T extends Texture> extends MaterialProperty<T> {
    private final int textureUnit;

    protected TextureMaterialProperty(T initialValue, int textureUnit, String name) {
        super(initialValue, name);
        if (textureUnit < 0) {
            throw new IllegalArgumentException("Texture unit must be nonnegative");
        }
        this.textureUnit = textureUnit;
    }

    public final int getTextureUnit() {
        return textureUnit;
    }

    @Override
    protected final void bindShaderData() {
        glBindTextureUnit(textureUnit, value == null ? 0 : value.getHandle());
    }

    @Override
    protected final void refreshShaderData(MemoryStack stack) {
        glUniform1i(location, textureUnit);
    }
}
