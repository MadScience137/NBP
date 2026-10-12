package ch.nbodyproblems.rendering;

import lombok.Getter;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class PerspectiveCamera {
    @Getter
    private float nearCLip;
    @Getter
    private float farCLip;
    @Getter
    private float aspectRatio;
    @Getter
    private float fov;
    @Getter
    private Matrix4f projection;
    @Getter
    private Vector3f position = new Vector3f();
    @Getter
    private Vector3f rotation = new Vector3f();

    public void setNearCLip(float nearCLip) {
        this.nearCLip = nearCLip;
        updateProjectionMatrix();
    }

    public void setFarCLip(float farCLip) {
        this.farCLip = farCLip;
        updateProjectionMatrix();
    }

    public void setAspectRatio(float aspectRatio) {
        this.aspectRatio = aspectRatio;
        updateProjectionMatrix();
    }

    public void setFov(float fov) {
        this.fov = fov;
        updateProjectionMatrix();
    }

    public void setPosition(Vector3f position) {
        this.position = position;
        updateProjectionMatrix();
    }

    public void setPosition(float x, float y, float z) {
        setPosition(new Vector3f(x, y, z));
    }

    public void setRotation(Vector3f rotation) {
        this.rotation = rotation;
        updateProjectionMatrix();
    }

    public void setRotation(float x, float y, float z) {
        setRotation(new Vector3f(x, y, z));
    }

    public PerspectiveCamera(float nearCLip, float farCLip, float aspectRatio, float fov) {
        this.nearCLip = nearCLip;
        this.farCLip = farCLip;
        this.aspectRatio = aspectRatio;
        this.fov = fov;

        updateProjectionMatrix();
    }

    private void updateProjectionMatrix() {
        projection = new Matrix4f().perspective(
                (float) Math.toRadians(fov),
                aspectRatio,
                farCLip, nearCLip,
                true
        ).mul(
                new Matrix4f()
                        .rotateXYZ(rotation)
                        .translate(position)
        );
    }
}
