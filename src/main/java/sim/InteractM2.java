package sim;

/**
 * Created by Chad on 3/8/15.
 */
public class InteractM2 implements Interact {
    public double EPSILON, MU, DELTA;

    public InteractM2(double EPSILON, double MU, double DELTA) {
        this.EPSILON = EPSILON;
        this.MU = MU;
        this.DELTA = DELTA;
    }

    public double opinionDiff(Agent a, Agent b) {
        double opA = a.getOpinion(), opB = b.getOpinion();
        double odiff = Math.abs(opB-opA);
        if(odiff <= EPSILON)
            return MU*(opB-opA);
        else if(opA <= DELTA && opB >= 1-DELTA) {
            double diff = -MU*opA;
            if(a.getOpinion()+diff<0)
                diff = -opA;
            return diff;
		} else if (opA >= 1 - DELTA && opB <= DELTA) {
            double diff = MU*(1-opA);
            if(a.getOpinion()+opA > 1)
                diff = 1-opA;
            return diff;
		} else
            return 0;
    }

    public IndepedentVariable updateValue(Independent I, double V) {
		if (I == Independent.EPSILON)
			return new InteractM2(V, MU, DELTA);
		else if (I == Independent.MU)
			return new InteractM2(EPSILON, V, DELTA);
		else if (I == Independent.DELTA)
			return new InteractM2(EPSILON, MU, V);
		else
			return new InteractM2(EPSILON, MU, DELTA);
    }
}
