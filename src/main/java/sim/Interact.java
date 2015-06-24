package sim;

/**
 * Created by Chad on 2/14/15.
 */
public interface Interact extends IndependentVariable {
    /**
     * Returns the difference in opinion that agent b has on agent a.
     *
     * @param a
     * @param b
     * @return
     */
    public double opinionDiff(Agent a, Agent b);
}
