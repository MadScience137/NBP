package ch.nbodyproblems;

import lombok.Getter;
import lombok.Setter;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class GameObject implements Tickable {
    @Setter
    @Getter
    private boolean enabled = true;

    @Getter
    private final Matrix4f transform = new Matrix4f()
            .translate(0f, 0f, 0f)
            .rotateXYZ(0f, 0f, 0f)
            .scale(1f);

    private final List<GameObject> children = new ArrayList<>();

    private final Map<Class<? extends Component>, Component> components = new HashMap<>();

    @Override
    public void tick(double time, double deltaTime) {
    }

    public void addComponent(Component component) {
        components.put(component.getClass(), component);
    }

    public <T extends Component> T getComponent(Class<T> componentClass) {
        return (T) components.get(componentClass);
    }

    public boolean hasComponent(Class<? extends Component> componentClass) {
        return components.containsKey(componentClass);
    }

    public void addChild(GameObject child) {
        children.add(child);
    }

    public void removeChild(GameObject child) {
        children.remove(child);
    }

    public List<GameObject> getChildren() {
        return List.copyOf(children);
    }

    public void setTransform(Matrix4f transform) {
        this.transform.set(transform);
    }

    public Vector3f getPosition() {
        return transform.getTranslation(new Vector3f());
    }

    public void setPosition(float x, float y, float z) {
        transform.translation(x, y, z);
    }

    public Vector3f getRotation() {
        return transform.getEulerAnglesXYZ(new Vector3f());
    }

    public void setRotation(float x, float y, float z) {
        transform.rotationXYZ(x, y, z);
    }

    public Vector3f getScale() {
        return transform.getScale(new Vector3f());
    }

    public void setScale(float scale) {
        transform.scale(scale);
    }

    public void setScale(float x, float y, float z) {
        transform.scale(x, y, z);
    }
}
