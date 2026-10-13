package ch.nbodyproblems.input.axis.activation;

public class LinearActivation extends ActivationFunction {
    public LinearActivation(double duration) {
        super(duration);
    }

    @Override
    public double apply(double time) {
        return Math.clamp(time / duration, 0, 1);
    }
}
