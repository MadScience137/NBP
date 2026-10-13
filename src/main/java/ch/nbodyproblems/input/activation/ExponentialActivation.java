package ch.nbodyproblems.input.activation;

public class ExponentialActivation extends ActivationFunction {
    private final double strength;

    public ExponentialActivation(double duration, double strength) {
        super(duration);
        this.strength = strength;
    }

    @Override
    public double apply(double time) {
        return applyExponential(Math.clamp(time / duration, 0, 1));
    }

    private double applyExponential(double linearValue) {
        return (Math.exp(strength * linearValue) - 1) / (Math.exp(strength) - 1);
    }
}
