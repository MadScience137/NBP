package ch.nbodyproblems.rendering.scene;

import lombok.Getter;
import lombok.Setter;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.*;

public abstract class GameObject implements Tickable {
    @Setter
    @Getter
    private boolean enabled = true;

    @Getter
    private final Matrix4f transform = new Matrix4f()
            .translate(0f, 0f, 0f)
            .rotateXYZ(0f, 0f, 0f)
            .scale(1f);

    private GameObject parent;
    private final List<GameObject> children = new ArrayList<>();

    private final Map<Class<? extends Component>, Component> components = new HashMap<>();

    @Override
    public final void tick(double time, double deltaTime) {
        if (enabled) {
            update(time, deltaTime);
            components.values().stream()
                    .filter(Component::isEnabled)
                    .filter(TickableComponent.class::isInstance)
                    .map(TickableComponent.class::cast)
                    .forEach(c -> c.tick(time, deltaTime));
        }
    }

    protected void update(double time, double deltaTime) {
    }

    public void addComponent(Component component) {
        components.put(component.getClass(), component);
    }

    public <T extends Component> T getComponent(Class<T> componentClass) {
        return (T) components.get(componentClass);
    }

    public void removeComponent(Class<? extends Component> componentClass) {
        components.remove(componentClass);
    }

    public <T extends Component> List<T> getComponents(Class<T> componentClass) {
        return components.values().stream()
                .filter(componentClass::isInstance)
                .map(componentClass::cast)
                .toList();
    }

    public boolean hasComponent(Class<? extends Component> componentClass) {
        return components.containsKey(componentClass);
    }

    public Optional<GameObject> getParent() {
        return Optional.ofNullable(parent);
    }

    public void addChild(GameObject child) {
        if (child == this) throw new IllegalArgumentException("Child cannot be itself");
        if (child.parent != null) throw new IllegalArgumentException("Child is already attached");
        child.parent = this;
        children.add(child);
    }

    public void removeChild(GameObject child) {
        child.parent = null;
        children.remove(child);
    }

    public List<GameObject> getChildren() {
        return List.copyOf(children);
    }

    public <T extends Component> List<T> getChildren(Class<T> childClass) {
        return children.stream()
                .filter(childClass::isInstance)
                .map(childClass::cast)
                .toList();
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
