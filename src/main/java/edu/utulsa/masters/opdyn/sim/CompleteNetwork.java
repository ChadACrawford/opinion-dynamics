package edu.utulsa.masters.opdyn.sim;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * A complete network implementation. A special class is used to save memory/speed up computation time.
 */
public class CompleteNetwork extends Network {
    public CompleteNetwork(int N, Random rand) {
        super(N, rand);
    }

    public List<Agent> neighbors(Agent a) {
        List<Agent> ns = new ArrayList<Agent>(agents);
        ns.remove(a);
        return ns;
    }

    public int degree(Agent a) {
        return agents.size()-1;
    }

    public void addEdge(Agent a, Agent b) {
        //do nothing
    }

    public void remEdge(Agent a, Agent b) {
        //do nothing
    }

    public boolean isEdge(Agent a, Agent b) {
        return (a != b);
    }
}
