package ch.nbodyproblems;

@FunctionalInterface
public interface Tickable {
    void tick(double time, double deltaTime);
}
