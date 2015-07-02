package stats;

import sim.Agent;
import sim.Simulator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

/**
 * Created by Chad on 3/9/15.
 */
public class TimedOpinionDistribution extends StatModule {
    private HashMap<Simulator, BufferedWriter> writerHashMap = new HashMap<Simulator, BufferedWriter>();
    private HashMap<Simulator, Double> sims = new HashMap<Simulator, Double>();
    private int trialCount = 1, roundCount = 1;
    private BufferedWriter f;

    // number of buckets to place opinions into
    private int B = 101;

    public TimedOpinionDistribution(Statistics parent) {
        super(parent);
    }

    @Override
    public void hookSimulationBegin() {
        File f = new File(stats.getDataFolder() + "Opinion_Distributions/Data");
        if (!f.isDirectory()) {
            f.mkdirs();
        }

        f = new File(stats.getDataFolder() + "Opinion_Distributions/Graphs");
        if (!f.isDirectory()) {
            f.mkdirs();
        }
    }


    @Override
    public void hookTrialBegin(Simulator sim, double indpVal, int trial) {
        sims.put(sim, indpVal);

        try {
            writerHashMap.put(sim, new BufferedWriter(new FileWriter(new File(stats.getDataFolder() + "Opinion_Distributions/Data/Trial " + trialCount + ".dat"))));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void hookTrialEnd(Simulator sim) {

        try {
            writerHashMap.get(sim).close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        writerHashMap.remove(sim);

        String[][] args = new String[][]{
                new String[]{"outfile",
                        stats.getDataFolder() + "Opinion_Distributions/Graphs/Trial " + trialCount},
                new String[]{"infile", stats.getDataFolder() + "Opinion_Distributions/Data/Trial " + trialCount + ".dat"},
                new String[]{"independent", "Time"},
                new String[]{"gtitle", "Opinion Density for Epsilon = " + ((double) ((Math.round(sims.get(sim) * 10000))) / 1000)}};
        Gnuplot.plotFile("opinion_distribution.plt", args);

        sims.remove(sim);
        trialCount++;
        roundCount = 0;

        return;
    }


    @Override
    public void hookRoundEnd(Simulator sim, int round) {
        Double[] b = new Double[B];
        for (int i = 0; i < B; i++) {
            b[i] = 0.;
        }

        for (Agent a : sim.getAgents()) {
            b[(int) Math.round(a.getOpinion() * (B - 1))]++;
        }

        f = writerHashMap.get(sim);
        StringBuilder s = new StringBuilder();

        if (roundCount % 100 == 0) {
            try {
                f.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            for (int i = 0; i < b.length; i++) {
                s.append(roundCount).append(" ").append(((double) i) / 100).append(" ").append(b[i]).append("\n");
            }
            f.write(s.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        roundCount++;
    }
}
