package ch.nbodyproblems.rendering.texture;

import java.nio.file.Path;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;

public final class Texture2D extends Texture {
    private Texture2D(TextureImage image) {
        super(GL_TEXTURE_2D, image);
    }

    public static Texture2D load(Path path) {
        return load(path, true);
    }

    public static Texture2D load(Path path, boolean flipVertically) {
        try (TextureImage image = TextureImage.load(path, flipVertically)) {
            return new Texture2D(image);
        }
    }

    public static Texture2D loadResource(String path) {
        return loadResource(path, true);
    }

    public static Texture2D loadResource(String path, boolean flipVertically) {
        try (TextureImage image = TextureImage.loadResource(path, flipVertically)) {
            return new Texture2D(image);
        }
    }
}
