package stats;

import sim.Simulator;

/**
 * Created by Chad on 3/8/15.
 */
public abstract class StatModule {
	Statistics stats;

	public StatModule(Statistics parent) {
		this.stats = parent;
	}

	public void hookSimulationBegin() {
	}

	public void hookSimulationEnd() {
	}

	public void hookTrialBegin(Simulator sim, double indpVal, int trial) {
	}

	public void hookTrialEnd(Simulator sim) {
	}

	public void hookRoundBegin(Simulator sim, int round) {
	}

	public void hookRoundEnd(Simulator sim, int round) {
	}
}
