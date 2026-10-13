package ch.nbodyproblems.input.activation;

public class BinaryActivation extends ActivationFunction {
    public BinaryActivation(double duration) {
        super(duration);
    }

    @Override
    public double apply(double time) {
        return time < duration ? 0 : 1;
    }
}
