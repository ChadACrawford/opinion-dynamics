package edu.utulsa.masters.opdyn.stats;

import edu.utulsa.masters.opdyn.sim.Simulator;

/**
 * Class that collects statistical information during a simulation, and then periodically writes to a file/files.
 *
 * To have a StatModule start tracking info, you will need to do:
 *
 * 1. Add an option for the StatModule in the .properties config file
 * 2. Write a check to create the module in Statistics.readConfig() (this could be improved to use a registry instead)
 * 3. Override methods to track the information that you want
 */
public abstract class StatModule {
    Statistics stats;

    public StatModule(Statistics parent) {
        this.stats = parent;
    }

    public void hookSimulationBegin() {}
    public void hookSimulationEnd() {}
    public void hookTrialBegin(Simulator sim, double indpVal, int trial) {}
    public void hookTrialEnd(Simulator sim) {}
    public void hookRoundBegin(Simulator sim, int round) {}
    public void hookRoundEnd(Simulator sim, int round) {}
}
