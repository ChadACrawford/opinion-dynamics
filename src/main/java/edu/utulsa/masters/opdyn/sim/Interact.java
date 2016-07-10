package edu.utulsa.masters.opdyn.sim;

/**
 * Helper class for computing opinion differences. If you would like to modify the way agents are influenced in
 * binary interactions, you should extend this interface with your own class.
 *
 * For some reason this extends IndependentVariable. I have no idea why, maybe I had a reason last year.
 */
public interface Interact extends IndepedentVariable {
    /**
     * Returns the difference in opinion that agent b has on agent a. Note that this is not necessarily bidirectional.
     * @param a Agent 1
     * @param b Agent 2
     * @return The difference magnitude, used to determine when to stop running.
     */
    public double opinionDiff(Agent a, Agent b);
}
