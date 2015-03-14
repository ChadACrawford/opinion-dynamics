package sim;

/**
 * Created by Chad on 3/8/15.
 */
public class InteractM1 implements Interact {
    public final double EPSILON, MU, DELTA;

    public InteractM1(double EPSILON, double MU, double DELTA) {
        this.EPSILON = EPSILON;
        this.MU = MU;
        this.DELTA = DELTA;
    }

    @Override
    public double opinionDiff(Agent a, Agent b) {
        return 0;
    }

    @Override
    public IndepedentVariable updateValue(Independent I, double V) {
        if(I == Independent.EPSILON) return new InteractM1(V, MU, DELTA);
        else if(I == Independent.MU) return new InteractM1(EPSILON, V, DELTA);
        else if(I == Independent.DELTA) return new InteractM1(EPSILON, MU, V);
        else return new InteractM1(EPSILON, MU, DELTA);
    }
}
