package ch.nbodyproblems.rendering.texture;

import ch.nbodyproblems.util.Destroyable;
import org.lwjgl.system.MemoryStack;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.lwjgl.stb.STBImage.*;
import static org.lwjgl.system.MemoryUtil.memAlloc;
import static org.lwjgl.system.MemoryUtil.memFree;

record TextureImage(int width, int height, ByteBuffer pixels) implements Destroyable {
    static TextureImage load(Path path, boolean flipVertically) {
        try {
            return decode(Files.readAllBytes(path), path.toString(), flipVertically);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to read texture: " + path, e);
        }
    }

    static TextureImage loadResource(String path, boolean flipVertically) {
        try (InputStream stream = TextureImage.class.getResourceAsStream(path)) {
            if (stream == null) {
                throw new IllegalStateException("Texture resource not found: " + path);
            }
            return decode(stream.readAllBytes(), path, flipVertically);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to read texture resource: " + path, e);
        }
    }

    private static TextureImage decode(byte[] bytes, String source, boolean flipVertically) {
        if (bytes.length == 0) {
            throw new IllegalArgumentException("Empty texture image: " + source);
        }

        ByteBuffer encoded = memAlloc(bytes.length);

        try (MemoryStack stack = MemoryStack.stackPush()) {
            encoded.put(bytes).flip();

            IntBuffer width = stack.mallocInt(1);
            IntBuffer height = stack.mallocInt(1);
            IntBuffer channels = stack.mallocInt(1);

            stbi_set_flip_vertically_on_load_thread(flipVertically ? 1 : 0);
            ByteBuffer pixels = stbi_load_from_memory(encoded, width, height, channels, STBI_rgb_alpha);

            if (pixels == null) {
                throw new IllegalArgumentException("Unable to decode texture " + source + ": " + stbi_failure_reason());
            }

            return new TextureImage(width.get(0), height.get(0), pixels);
        } finally {
            memFree(encoded);
        }
    }

    @Override
    public void destroy() {
        stbi_image_free(pixels);
    }
}
