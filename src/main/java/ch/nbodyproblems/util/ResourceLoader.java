package ch.nbodyproblems.util;

import ch.nbodyproblems.Main;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class ResourceLoader {
    public static String loadResource(String path) {
        try (InputStream inputStream = Main.class.getResourceAsStream(path)) {
            if (inputStream == null) throw new IllegalStateException("Resource not found: " + path);
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to read resource: " + path, e);
        }
    }
}
