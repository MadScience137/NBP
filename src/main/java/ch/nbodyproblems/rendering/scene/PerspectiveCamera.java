package ch.nbodyproblems.rendering.scene;

import lombok.Getter;
import lombok.Setter;
import org.joml.Matrix4f;
import org.joml.Quaterniond;
import org.joml.Quaternionf;
import org.joml.Vector3d;

public class PerspectiveCamera {
    @Setter
    @Getter
    private double nearCLip;
    @Setter
    @Getter
    private double farCLip;
    @Setter
    @Getter
    private double aspectRatio;
    @Setter
    @Getter
    private double fov;
    private Matrix4f cameraMatrix = new Matrix4f();
    private Matrix4f viewMatrix = new Matrix4f();
    @Getter
    private Matrix4f projectionMatrix = new Matrix4f();
    @Setter
    private Vector3d position = new Vector3d();
    private Quaterniond rotation = new Quaterniond();

    public PerspectiveCamera(double nearCLip, double farCLip, double aspectRatio, double fov) {
        this.nearCLip = nearCLip;
        this.farCLip = farCLip;
        this.aspectRatio = aspectRatio;
        this.fov = fov;

        updateProjectionMatrix();
    }

    public void setPosition(double x, double y, double z) {
        setPosition(new Vector3d(x, y, z));
    }

    public void move(double x, double y, double z) {
        position.add(x, y, z);
    }

    public void move(Vector3d delta) {
        position.add(delta);
    }

    public void moveLocal(double x, double y, double z) {
        move(rotation.transform(new Vector3d(x, y, z)));
    }

    public void setRotation(Vector3d newRotation) {
        rotation.rotationZYX(
                Math.toRadians(newRotation.z),
                Math.toRadians(newRotation.y),
                Math.toRadians(newRotation.x)
        );
    }

    public void rotate(double pitch, double yaw, double roll) {
        rotation.premul(new Quaterniond().rotationY(Math.toRadians(yaw)));
        rotation.rotateX(Math.toRadians(pitch));
        rotation.rotateZ(Math.toRadians(roll));
        rotation.normalize();
    }

    public void setRotation(double pitch, double yaw, double roll) {
        setRotation(new Vector3d(pitch, yaw, roll));
    }

    public void updateProjectionMatrix() {
        updateCameraMatrix();
        viewMatrix.identity()
                .rotate(new Quaternionf(rotation).invert())
                .translate((float) position.x, (float) position.y, (float) position.z);

        projectionMatrix.set(cameraMatrix).mul(viewMatrix);
    }

    private void updateCameraMatrix() {
        cameraMatrix.identity().perspective(
                (float) Math.toRadians(fov),
                (float) aspectRatio,
                (float) farCLip, (float) nearCLip,
                true
        );
    }

    public void reset() {
        position.set(0, 0, 0);
        rotation.identity();
    }
}
