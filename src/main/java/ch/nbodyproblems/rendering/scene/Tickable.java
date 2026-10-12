package ch.nbodyproblems.rendering.scene;

@FunctionalInterface
public interface Tickable {
    void tick(double time, double deltaTime);
}
