package ch.nbodyproblems.input.axis.activation;

public abstract class ActivationFunction {
    protected final double duration;

    protected ActivationFunction(double duration) {
        this.duration = duration;
    }

    public abstract double apply(double time);
}
