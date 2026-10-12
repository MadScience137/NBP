package ch.nbodyproblems.util;

public interface Destroyable extends AutoCloseable {
    @Override
    default void close() {
        destroy();
    }

    void destroy();
}
