package ch.nbodyproblems;

import org.joml.Matrix4f;
import org.joml.Vector3f;
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
    private Matrix4f projection;

    private Matrix4f model;
    private TriangleMesh triangleMesh;

    public Renderer(int initialWidth, int initialHeight) {
        this.initialWidth = initialWidth;
        this.initialHeight = initialHeight;
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
        updateProjectionMatrix((float) initialWidth, initialHeight);

        if (window == NULL) throw new IllegalStateException("Unable to create GLFW window");

        glfwSetFramebufferSizeCallback(window, (win, width, height) -> {
            glViewport(0, 0, width, height);
            updateProjectionMatrix((float) width, height);
        });

        glfwSetKeyCallback(window, (handle, key, scancode, action, mods) -> {
            if (key == GLFW_KEY_ESCAPE && action == GLFW_PRESS) {
                glfwSetWindowShouldClose(handle, true);
            }
        });

        glfwMakeContextCurrent(window);
        glfwShowWindow(window);

        GLCapabilities capabilities = GL.createCapabilities();
        if (!capabilities.OpenGL45) throw new IllegalStateException("OpenGL 4.5 is required");

        initTriangle();
    }

    private void updateProjectionMatrix(float width, int height) {
        projection = new Matrix4f().perspective(
                (float) Math.toRadians(60.0f),
                width / height,
                0.1f, 10_000.0f
        );
    }

    public void loop() {
        int shader = createShaderProgram();
        Matrix4f view = new Matrix4f();

        while (!glfwWindowShouldClose(window)) {
            glfwPollEvents();

            glClearColor(0f, 0f, 0f, 1f);
            glClear(GL_COLOR_BUFFER_BIT);

            view.identity().lookAt(
                    new Vector3f(0, 0, -3),
                    new Vector3f(0, 0, 0),
                    new Vector3f(0, 1, 0)
            );

            int projLoc = glGetUniformLocation(shader, "projection");
            int viewLoc = glGetUniformLocation(shader, "view");
            int modelLoc = glGetUniformLocation(shader, "model");

            glUseProgram(shader);

            try (MemoryStack stack = MemoryStack.stackPush()) {
                FloatBuffer fb = stack.mallocFloat(16);
                glUniformMatrix4fv(projLoc, false, projection.get(fb));
                glUniformMatrix4fv(viewLoc, false, view.get(fb));
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
