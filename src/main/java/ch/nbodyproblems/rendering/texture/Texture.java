package ch.nbodyproblems.rendering.texture;


import ch.nbodyproblems.util.Destroyable;

import static org.lwjgl.opengl.GL45.*;

public abstract class Texture implements Destroyable {
    private int handle;
    private final int width;
    private final int height;

    Texture(int target, TextureImage... images) {
        width = images[0].width();
        height = images[0].height();
        boolean cubeMap = target == GL_TEXTURE_CUBE_MAP;

        for (TextureImage image : images) {
            if (image.width() != width || image.height() != height || (cubeMap && width != height)) {
                throw new IllegalArgumentException("Cube map faces must be equally sized squares");
            }
        }

        int maxSize = glGetInteger(cubeMap ? GL_MAX_CUBE_MAP_TEXTURE_SIZE : GL_MAX_TEXTURE_SIZE);

        if (width > maxSize || height > maxSize) {
            throw new IllegalArgumentException("Image exceeds maximum texture size: " + maxSize);
        }

        handle = glCreateTextures(target);
        if (handle == 0) throw new IllegalStateException("Unable to create texture");

        try {
            int levels = Integer.SIZE - Integer.numberOfLeadingZeros(Math.max(width, height));
            glTextureStorage2D(handle, levels, GL_RGBA8, width, height);

            uploadPixels(() -> {
                if (cubeMap) {
                    for (int face = 0; face < images.length; face++) {
                        glTextureSubImage3D(handle, 0,
                                0, 0,
                                face,
                                width, height,
                                1,
                                GL_RGBA, GL_UNSIGNED_BYTE,
                                images[face].pixels()
                        );
                    }
                } else {
                    glTextureSubImage2D(handle, 0,
                            0, 0,
                            width, height,
                            GL_RGBA, GL_UNSIGNED_BYTE,
                            images[0].pixels()
                    );
                }
            });

            glTextureParameteri(handle, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
            glTextureParameteri(handle, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
            int wrap = cubeMap ? GL_CLAMP_TO_EDGE : GL_REPEAT;
            glTextureParameteri(handle, GL_TEXTURE_WRAP_S, wrap);
            glTextureParameteri(handle, GL_TEXTURE_WRAP_T, wrap);
            if (cubeMap) glTextureParameteri(handle, GL_TEXTURE_WRAP_R, wrap);

            glGenerateTextureMipmap(handle);
        } catch (RuntimeException | Error e) {
            destroy();
            throw e;
        }
    }

    public final int getHandle() {
        if (isDestroyed()) throw new IllegalStateException("Texture is destroyed");
        return handle;
    }

    public final int getWidth() {
        return width;
    }

    public final int getHeight() {
        return height;
    }

    public final boolean isDestroyed() {
        return handle == 0;
    }

    @Override
    public final void destroy() {
        if (isDestroyed()) return;
        glDeleteTextures(handle);
        handle = 0;
    }

    private static void uploadPixels(Runnable upload) {
        int[] parameters = {
                GL_UNPACK_ALIGNMENT, GL_UNPACK_ROW_LENGTH, GL_UNPACK_IMAGE_HEIGHT,
                GL_UNPACK_SKIP_PIXELS, GL_UNPACK_SKIP_ROWS, GL_UNPACK_SKIP_IMAGES
        };
        glBindBuffer(GL_PIXEL_UNPACK_BUFFER, 0);

        for (int parameter : parameters) {
            glPixelStorei(parameter, parameter == GL_UNPACK_ALIGNMENT ? 1 : 0);
        }

        upload.run();
    }
}
