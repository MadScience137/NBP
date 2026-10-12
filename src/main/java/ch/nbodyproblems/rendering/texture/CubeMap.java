package ch.nbodyproblems.rendering.texture;

import java.nio.file.Path;
import java.util.Objects;
import java.util.function.IntFunction;

import static org.lwjgl.opengl.GL13.GL_TEXTURE_CUBE_MAP;

public final class CubeMap extends Texture {
    private CubeMap(TextureImage[] images) {
        super(GL_TEXTURE_CUBE_MAP, images);
    }

    /**
     * Loads six image files with their original row order.
     *
     * @param faces file paths in +X, -X, +Y, -Y, +Z, -Z order
     * @return a cube map the caller must close with its OpenGL context current
     */
    public static CubeMap load(Path... faces) {
        requireSixFaces(faces);
        return loadImages(face -> TextureImage.load(faces[face], false));
    }

    /**
     * Loads six image files with their original row order.
     *
     * @param faces file paths in +X, -X, +Y, -Y, +Z, -Z order
     * @return a cube map the caller must close with its OpenGL context current
     */
    public static CubeMap loadResources(String... faces) {
        requireSixFaces(faces);
        return loadImages(face -> TextureImage.loadResource(faces[face], false));
    }

    private static void requireSixFaces(Object[] faces) {
        if (faces == null || faces.length != 6) {
            throw new IllegalArgumentException("A cube map requires exactly six faces");
        }
        for (Object face : faces) Objects.requireNonNull(face, "Cube map face must not be null");
    }

    private static CubeMap loadImages(IntFunction<TextureImage> loader) {
        TextureImage[] images = new TextureImage[6];
        try {
            for (int face = 0; face < images.length; face++) images[face] = loader.apply(face);
            return new CubeMap(images);
        } finally {
            for (TextureImage image : images) {
                if (image != null) image.destroy();
            }
        }
    }
}
