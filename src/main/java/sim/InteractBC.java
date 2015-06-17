package sim;

/**
 * Created by Chad on 3/8/15.
 */
public class InteractBC implements Interact {
	public final double EPSILON;
	public final double MU;

	public InteractBC(double EPSILON, double MU) {
		this.EPSILON = EPSILON;
		this.MU = MU;
	}

	public double opinionDiff(Agent a, Agent b) {
		double oa = a.getOpinion(), ob = b.getOpinion();
		if (Math.abs(oa - ob) < EPSILON) {
			Debug.format(40, "%8.6f %8.6f %6.4f\n", oa, ob, MU * (ob - oa));
			return MU * (ob - oa);
		} else
			return 0.;
	}

	public IndepedentVariable updateValue(Independent I, double V) {
		if (I == Independent.EPSILON)
			return new InteractBC(V, MU);
		else if (I == Independent.MU)
			return new InteractBC(EPSILON, V);
		else
			return new InteractBC(EPSILON, MU);
	}
}
