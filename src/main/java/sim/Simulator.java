package sim;

import java.util.List;
import java.util.Random;

/**
 * Created by Chad on 2/14/15.
 */
public abstract class Simulator {
	protected int T = 0;
	public final Interact I;
	long seed;
	Random rand;

	protected double tOpinionDifference;
	// Number of Interactions (Attraction, Repulsion).
	protected int rI;
	protected double maxOpinionDifference;
	protected double minOpinionDifference;

	private final boolean TRACK_ACTIVITY;

	public Simulator(Interact I, boolean TRACK_ACTIVITY, long seed) {
		this.I = I;
		this.TRACK_ACTIVITY = TRACK_ACTIVITY;
		this.seed = seed;
		rand = new Random(this.seed);
	}

	public int getTime() {
		return T;
	}

	public void interact(Agent a, Agent b) {
		/*
		 * if (Math.abs(0.5 - a.getOpinion()) > Math.abs(0.5 - b.getOpinion()))
		 * { Agent t = b; b = a; a = t; }
		 */

		double odA = I.opinionDiff(a, b);
		double odB = I.opinionDiff(b, a);
		if (odA == 0)
			return;
		rI += 1;
		tOpinionDifference += Math.abs(odA);// +odB;
		if (odA > maxOpinionDifference)
			maxOpinionDifference = odA;
		if (odB > maxOpinionDifference)
			maxOpinionDifference = odB;
		if (odA < minOpinionDifference)
			minOpinionDifference = odA;
		if (odB < minOpinionDifference)
			minOpinionDifference = odB;

		if (TRACK_ACTIVITY) {
			a.perturbOpinion(odA, b.getInfluence());
		} else {
			a.perturbOpinion(odA);
			b.perturbOpinion(odB);
		}
	}

	public double getMeanOpinionDifference() {
		return tOpinionDifference / rI;
	}

	public double getTotalOpinionDifference() {
		return tOpinionDifference;
	}

	public double getMaxOpinionDifference() {
		return maxOpinionDifference;
	}

	public double getMinOpinionDifference() {
		return minOpinionDifference;
	}

	public abstract List<Agent> getAgents();

	private void resetOpinionValues() {
		tOpinionDifference = 0;
		rI = 0;
		maxOpinionDifference = 0;
		minOpinionDifference = 0;
	}

	public void runRound() {
		T++;
		resetOpinionValues();
		round();
	}

	public abstract void round();

}
