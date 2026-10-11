package ch.nbodyproblems;

import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GLCapabilities;
import org.lwjgl.system.MemoryStack;

import java.io.IOException;
import java.io.InputStream;
import java.nio.FloatBuffer;
import java.nio.charset.StandardCharsets;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glDeleteVertexArrays;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Renderer {
    private static final String VERTEX_SHADER_PATH = "/shaders/triangle.vert";
    private static final String FRAGMENT_SHADER_PATH = "/shaders/triangle.frag";

    private final int initialWidth;
    private final int initialHeight;

    private long window;

    private final PerspectiveCamera camera;

    private Matrix4f model;
    private TriangleMesh triangleMesh;

    public Renderer(int initialWidth, int initialHeight) {
        this.initialWidth = initialWidth;
        this.initialHeight = initialHeight;
        float aspectRatio = (float) initialWidth / initialHeight;
        camera = new PerspectiveCamera(0.1f, 1e4f, aspectRatio, 60f);
        camera.setPosition(0f, 0f, -4f);
    }

    public void init() {
        GLFWErrorCallback.createPrint(System.err).set();
        if (!glfwInit()) throw new IllegalStateException("Unable to initialize GLFW");

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 4);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 5);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);

        window = glfwCreateWindow(initialWidth, initialHeight, "NBP", NULL, NULL);

        if (window == NULL) throw new IllegalStateException("Unable to create GLFW window");

        glfwSetFramebufferSizeCallback(window, (win, width, height) -> {
            glViewport(0, 0, width, height);
            camera.setAspectRatio((float) width / (float) height);
        });

        glfwMakeContextCurrent(window);
        glfwShowWindow(window);

        GLCapabilities capabilities = GL.createCapabilities();
        if (!capabilities.OpenGL45) throw new IllegalStateException("OpenGL 4.5 is required");

        initTriangle();
    }

    public void loop() {
        int shader = createShaderProgram();

        while (!glfwWindowShouldClose(window)) {
            glfwPollEvents();

            glClearColor(0f, 0f, 0f, 1f);
            glClear(GL_COLOR_BUFFER_BIT);

            int projLoc = glGetUniformLocation(shader, "projection");
            int modelLoc = glGetUniformLocation(shader, "model");

            glUseProgram(shader);

            try (MemoryStack stack = MemoryStack.stackPush()) {
                FloatBuffer fb = stack.mallocFloat(16);
                glUniformMatrix4fv(projLoc, false, camera.getProjection().get(fb));
                glUniformMatrix4fv(modelLoc, false, model.get(fb));
            }

            glBindVertexArray(triangleMesh.vao);
            glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0);

            glfwSwapBuffers(window);
        }
    }

    public void cleanup() {
        glDeleteBuffers(triangleMesh.vbo);
        glDeleteVertexArrays(triangleMesh.vao);
        glfwDestroyWindow(window);
        glfwTerminate();
    }

    private static String loadResource(String path) {
        try (InputStream inputStream = Main.class.getResourceAsStream(path)) {
            if (inputStream == null) throw new IllegalStateException("Resource not found: " + path);
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to read resource: " + path, e);
        }
    }

    private void initTriangle() {
        float[] vertices = {
                -1f, 1f, 0f,
                -1f, -1f, 0f,
                1f, -1f, 0f,
                1f, 1f, 0f
        };

        int[] triangles = {
                0, 1, 2,
                2, 3, 0
        };

        triangleMesh = new TriangleMesh(vertices, triangles);

        model = new Matrix4f()
                .translate(0f, 0f, 0f)
                .rotateXYZ(0f, 0f, 0f)
                .scale(1f);
    }

    private static int createShaderProgram() {
        String vertexSource = loadResource(VERTEX_SHADER_PATH);
        String fragmentSource = loadResource(FRAGMENT_SHADER_PATH);

        int vs = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(vs, vertexSource);
        glCompileShader(vs);

        int fs = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(fs, fragmentSource);
        glCompileShader(fs);

        int program = glCreateProgram();
        glAttachShader(program, vs);
        glAttachShader(program, fs);
        glLinkProgram(program);

        glDeleteShader(vs);
        glDeleteShader(fs);

        return program;
    }
}
