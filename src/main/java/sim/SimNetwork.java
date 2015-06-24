package sim;

import cutil.CList;

import java.util.List;

/**
 * Created by Chad on 3/8/15.
 */
public class SimNetwork extends Simulator {
    private final Network N;

    public SimNetwork(Interact I, boolean TRACK_ACTIVITY, long seed, Network N) {
        super(I, TRACK_ACTIVITY, seed);
        this.N = N;
    }

    @Override
    public void round() {
        for (Agent a : N.agents) {
            Agent b = CList.choose(rand, N.neighbors(a));
            interact(a, b);
        }
    }

    public List<Agent> getAgents() {
        return N.agents;
    }

    public Network getNetwork() {
        return N;
    }
}
