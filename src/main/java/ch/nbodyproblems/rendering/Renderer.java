package ch.nbodyproblems.rendering;

import ch.nbodyproblems.rendering.material.MaterialBase;
import ch.nbodyproblems.rendering.mesh.MeshComponent;
import ch.nbodyproblems.rendering.mesh.TriangleMesh;
import ch.nbodyproblems.rendering.mesh.primitives.Quad;
import ch.nbodyproblems.rendering.scene.GameObject;
import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GLCapabilities;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15C.glDeleteBuffers;
import static org.lwjgl.opengl.GL20.glUseProgram;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glDeleteVertexArrays;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Renderer {
    private final int initialWidth;
    private final int initialHeight;

    private long window;

    private final PerspectiveCamera camera;

    private List<GameObject> sceneObjects = new ArrayList<>();

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

        initScene();
    }

    private void initScene() {
        sceneObjects.add(new Quad(0.1f, 0.2f, 0.4f, 1f));
    }

    public void loop() {
        Matrix4f rootTransform = new Matrix4f().translate(0f, 0f, 0f).scale(1);

        while (!glfwWindowShouldClose(window)) {
            glfwPollEvents();

            glClearColor(0f, 0f, 0f, 1f);
            glClear(GL_COLOR_BUFFER_BIT);

            for (GameObject object : sceneObjects) {
                drawSceneObject(object, rootTransform);
            }

            glfwSwapBuffers(window);
        }
    }

    private void drawSceneObject(GameObject object, Matrix4f parentTransform) {
        Matrix4f objectTransform = new Matrix4f(parentTransform).mul(object.getTransform());

        if (object.hasComponent(MeshComponent.class)) {
            MeshComponent meshComponent = object.getComponent(MeshComponent.class);

            MaterialBase material = meshComponent.getMaterial();
            glUseProgram(material.shaderProgram);
            material.setTransformMatrix(objectTransform);
            material.setProjectionMatrix(camera.getProjection());
            material.updateShaderData();

            glBindVertexArray(meshComponent.getMesh().vao);
            glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0);
        }

        for (GameObject child : object.getChildren()) {
            drawSceneObject(child, objectTransform);
        }
    }

    public void cleanup() {
        for (GameObject object : sceneObjects) {
            cleanUpSceneObject(object);
        }
        glfwDestroyWindow(window);
        glfwTerminate();
    }

    private void cleanUpSceneObject(GameObject object) {
        if (object.hasComponent(MeshComponent.class)) {
            MeshComponent meshComponent = object.getComponent(MeshComponent.class);
            TriangleMesh mesh = meshComponent.getMesh();
            glDeleteBuffers(mesh.vbo);
            glDeleteVertexArrays(mesh.vao);
        }

        for (GameObject child : object.getChildren()) {
            cleanUpSceneObject(child);
        }
    }
}
