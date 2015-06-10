package sim;

import java.util.HashMap;

/**
 * Created by Chad on 2/13/15.
 */
public class Agent {
	private static int AGENT_ID = 0;
	public static final int id = AGENT_ID++;
	private double opinion = -1;
	private HashMap<Agent, Double> influence = new HashMap<Agent, Double>();

	public Agent() {
		influence.put(this, 1.);
	}

	public Agent(double opinion) {
		this.opinion = opinion;
		influence.put(this, 1.);
	}

	public void setOpinion(double o) {
		this.opinion = o;
	}

	public void perturbOpinion(double opd) {
		this.opinion += opd;
	}

	public void perturbOpinion(double opd, HashMap<Agent, Double> influences) {
		this.perturbOpinion(opd);
		double r = opd / (influences.size());
		for (Agent a : influences.keySet()) {
			// Updates influence in HashMap if already exists. Otherwise, adds to HashMap.
			if (influence.containsKey(a))
				influence.replace(a, influence.get(a) + r * influences.get(a));
			else
				influence.put(a, r * influences.get(a));
		}
	}

	public double getOpinion() {
		return opinion;
	}

	public HashMap<Agent, Double> getInfluence() {
		return influence;
	}
}
