package ch.nbodyproblems.rendering;

import ch.nbodyproblems.input.InputSystem;
import ch.nbodyproblems.input.action.AxisInputAction;
import ch.nbodyproblems.rendering.material.Material;
import ch.nbodyproblems.rendering.material.UnlitMaterial;
import ch.nbodyproblems.rendering.mesh.MeshComponent;
import ch.nbodyproblems.rendering.mesh.TriangleMesh;
import ch.nbodyproblems.rendering.scene.GameObject;
import ch.nbodyproblems.rendering.scene.PerspectiveCamera;
import ch.nbodyproblems.rendering.scene.objects.Cube;
import ch.nbodyproblems.rendering.scene.objects.Quad;
import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GLCapabilities;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static ch.nbodyproblems.input.InputContext.EDITOR_VIEW;
import static ch.nbodyproblems.input.action.TriggerInputAction.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15C.glDeleteBuffers;
import static org.lwjgl.opengl.GL20.GL_LOWER_LEFT;
import static org.lwjgl.opengl.GL20.glUseProgram;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL45.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Renderer {
    private int width;
    private int height;
    private int framebufferWidth;
    private int framebufferHeight;

    private long window;
    private long lastResizeNano;

    private final PerspectiveCamera camera;

    private List<GameObject> sceneObjects = new ArrayList<>();
    private int framebuffer;
    private int colorTexture;
    private int depthTexture;

    private InputSystem inputSystem;

    public Renderer(int initialWidth, int initialHeight) {
        this.width = initialWidth;
        this.height = initialHeight;
        float aspectRatio = (float) initialWidth / initialHeight;
        camera = new PerspectiveCamera(0.1f, 1e4f, aspectRatio, 60f);
        camera.setPosition(0f, 0f, -5f);
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
        glfwWindowHint(GLFW_DEPTH_BITS, 0);

        window = glfwCreateWindow(width, height, "NBP", NULL, NULL);

        if (window == NULL) throw new IllegalStateException("Unable to create GLFW window");

        glfwSetFramebufferSizeCallback(window, (win, width, height) -> {
            this.width = width;
            this.height = height;
            lastResizeNano = System.nanoTime();
        });

        glfwMakeContextCurrent(window);

        GLCapabilities capabilities = GL.createCapabilities();
        if (!capabilities.OpenGL45) throw new IllegalStateException("OpenGL 4.5 is required");

        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer pixelWidth = stack.mallocInt(1);
            IntBuffer pixelHeight = stack.mallocInt(1);
            glfwGetFramebufferSize(window, pixelWidth, pixelHeight);
            width = pixelWidth.get(0);
            height = pixelHeight.get(0);
        }

        initFrameBuffer();
        initInputSystem();
        initScene();
        glfwShowWindow(window);
    }

    private void initInputSystem() {
        inputSystem = new InputSystem(window);
        inputSystem.requestContextSwitch(EDITOR_VIEW);
        inputSystem.addOnAction(CAMERA_RESET, camera::reset);
        inputSystem.addOnAction(MOUSE_CAPTURE_TOGGLE, () -> inputSystem.setMouseCaptured(!inputSystem.isMouseCaptured()));
        inputSystem.addOnAction(MOUSE_CAPTURE_RELEASE, () -> inputSystem.setMouseCaptured(false));
    }

    private void initFrameBuffer() {
        glClipControl(GL_LOWER_LEFT, GL_ZERO_TO_ONE);
        glEnable(GL_DEPTH_TEST);
        glDepthFunc(GL_GREATER);
        glFrontFace(GL_CCW);
        glDepthMask(true);
        glClearDepth(0.0);

        framebuffer = glCreateFramebuffers();
        updateFrameBuffer();
    }

    private void updateFrameBuffer() {
        destroyFrameTextures();
        depthTexture = glCreateTextures(GL_TEXTURE_2D);
        colorTexture = glCreateTextures(GL_TEXTURE_2D);

        glTextureStorage2D(
                colorTexture,
                1,
                GL_RGBA8,
                width,
                height
        );

        glTextureStorage2D(
                depthTexture,
                1,
                GL_DEPTH_COMPONENT32F,
                width,
                height
        );

        glNamedFramebufferTexture(
                framebuffer,
                GL_DEPTH_ATTACHMENT,
                depthTexture,
                0
        );

        glNamedFramebufferTexture(
                framebuffer,
                GL_COLOR_ATTACHMENT0,
                colorTexture,
                0
        );

        if (glCheckNamedFramebufferStatus(framebuffer, GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) {
            throw new IllegalStateException("Render framebuffer is incomplete");
        }

        framebufferWidth = width;
        framebufferHeight = height;
        camera.setAspectRatio((float) framebufferWidth / framebufferHeight);
    }

    private void initScene() {
        GameObject cube = new Cube(2, new UnlitMaterial(0.1f, 0.2f, 0.4f, 1f));
        GameObject quad = new Quad(4, new UnlitMaterial(0.4f, 0.2f, 0.1f, 1f));
        quad.setPosition(1, 0, -1);
        sceneObjects.add(cube);
        sceneObjects.add(quad);
    }

    private boolean frameBufferUpdateRequired() {
        return (width != framebufferWidth || height != framebufferHeight)
                && width > 0 && height > 0
                && System.nanoTime() - lastResizeNano > 8e7;
    }

    public void loop() {
        Matrix4f rootTransform = new Matrix4f().translate(0f, 0f, 0f).scale(1);
        long lastFrameTime = System.nanoTime();

        while (!glfwWindowShouldClose(window)) {
            long nowNano = System.nanoTime();
            double nowSeconds = nowNano / 1e9;
            double delta = (nowNano - lastFrameTime) / 1e9;
            lastFrameTime = nowNano;

            inputSystem.clearAxisActivations();
            glfwPollEvents();
            inputSystem.executeContextSwitch();
            inputSystem.tick(nowSeconds, delta);

            if (width <= 0 || height <= 0) {
                glfwWaitEvents();
                continue;
            }

            camera.moveLocal(
                    inputSystem.getAxisActivation(AxisInputAction.CAMERA_MOVE_LATERAL),
                    inputSystem.getAxisActivation(AxisInputAction.CAMERA_MOVE_VERTICAL),
                    inputSystem.getAxisActivation(AxisInputAction.CAMERA_MOVE_LONGITUDINAL)
            );
            camera.rotate(
                    inputSystem.getAxisActivation(AxisInputAction.CAMERA_ROTATE_PITCH),
                    inputSystem.getAxisActivation(AxisInputAction.CAMERA_ROTATE_YAW),
                    0f
            );
            camera.updateProjectionMatrix();

            if (frameBufferUpdateRequired()) {
                updateFrameBuffer();
            }

            glBindFramebuffer(GL_FRAMEBUFFER, framebuffer);
            glViewport(0, 0, framebufferWidth, framebufferHeight);
            glClearColor(0f, 0f, 0f, 1f);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            for (GameObject object : sceneObjects) {
                traverseSceneObjects(object, rootTransform, nowSeconds, delta);
            }

            glBindFramebuffer(GL_DRAW_FRAMEBUFFER, 0);
            glBlitNamedFramebuffer(
                    framebuffer,
                    0,
                    0, 0, framebufferWidth, framebufferHeight,
                    0, 0, width, height,
                    GL_COLOR_BUFFER_BIT,
                    GL_NEAREST
            );

            glfwSwapBuffers(window);
        }
    }

    private void traverseSceneObjects(GameObject object, Matrix4f parentTransform, double now, double delta) {
        if (!object.isEnabled()) return;

        object.tick(now, delta);
        Matrix4f objectTransform = new Matrix4f(parentTransform).mul(object.getTransform());

        if (object.hasComponent(MeshComponent.class)) {
            MeshComponent meshComponent = object.getComponent(MeshComponent.class);

            if (meshComponent.isEnabled()) {
                TriangleMesh mesh = meshComponent.getMesh();
                Material material = meshComponent.getMaterial();
                glUseProgram(material.shaderProgram);
                material.setTransformMatrix(objectTransform);
                material.setProjectionMatrix(camera.getProjectionMatrix());
                material.updateShaderData();

                switch (mesh.cullFace) {
                    case BACK ->{
                        glEnable(GL_CULL_FACE);
                        glCullFace(GL_BACK);
                    }
                    case FRONT -> {
                        glEnable(GL_CULL_FACE);
                        glCullFace(GL_FRONT);
                    }
                    case NONE -> glDisable(GL_CULL_FACE);
                }

                glBindVertexArray(mesh.vao);
                glDrawElements(GL_TRIANGLES, mesh.elementCount, GL_UNSIGNED_INT, 0);
            }
        }

        for (GameObject child : object.getChildren()) {
            traverseSceneObjects(child, objectTransform, now, delta);
        }
    }

    private void destroyFrameBuffer() {
        glDeleteFramebuffers(framebuffer);
        destroyFrameTextures();
    }

    private void destroyFrameTextures() {
        if (colorTexture == 0 || depthTexture == 0) return;
        glDeleteTextures(colorTexture);
        glDeleteTextures(depthTexture);
    }

    public void cleanup() {
        for (GameObject object : sceneObjects) {
            cleanUpSceneObject(object);
        }
        destroyFrameBuffer();
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
