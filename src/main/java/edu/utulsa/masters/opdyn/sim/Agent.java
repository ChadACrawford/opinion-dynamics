package edu.utulsa.masters.opdyn.sim;

import java.util.HashMap;

/**
 * Represents an agent in the network.
 */
public class Agent {
    // Static ID tracker
    private static int AGENT_ID = 0;

    // Unique agent ID
    public static final int id = AGENT_ID++;

    // Opinion value of agent in [0,1] generally
    private double opinion = -1;

    // Neighbors of agent.
    private HashMap<Agent,Double> influence = new HashMap<Agent,Double>();

    public Agent() {
        influence.put(this, 1.);
    }

    public Agent(double opinion) {
        this.opinion = opinion;
        influence.put(this, 1.);
    }

    public void perturbOpinion(double opd) {
        this.opinion += opd;
    }

    public void perturbOpinion(double opd, HashMap<Agent, Double> influences) {
        this.perturbOpinion(opd);
        double r = opd / (influences.size());
        for(Agent a: influences.keySet()) {
            if(influence.containsKey(a))
                influence.replace(a, influence.get(a)+r*influences.get(a));
            else
                influence.put(a, r*influences.get(a));
        }
    }

    public double getOpinion() {
        return opinion;
    }
    public void setOpinion(double o) {
        this.opinion = o;
    }

    public HashMap<Agent, Double> getInfluence() {
        return influence;
    }
}
