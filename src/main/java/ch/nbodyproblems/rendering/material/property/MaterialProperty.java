package ch.nbodyproblems.rendering.material.property;

import lombok.Getter;
import org.lwjgl.system.MemoryStack;

import static org.lwjgl.opengl.GL20.glGetUniformLocation;

public abstract class MaterialProperty<T> {
    protected T value;

    @Getter
    private final String name;

    private boolean isDirty;

    protected int location = -1;

    protected MaterialProperty(T initialValue, String name) {
        this.name = name;
        this.value = initialValue;
        this.isDirty = true;
    }

    public T get() {
        return value;
    }

    public void set(T value) {
        this.value = value;
        this.isDirty = true;
    }

    public final void updateShaderData(MemoryStack stack, int shaderProgram) {
        bindShaderData();
        if (!isDirty) return;
        if (location < 0) {
            location = glGetUniformLocation(shaderProgram, name);
        }
        refreshShaderData(stack);
        isDirty = false;
    }

    protected void bindShaderData() {
    }

    protected abstract void refreshShaderData(MemoryStack stack);
}
