package stats;

import sim.Agent;
import sim.Independent;
import sim.Simulator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Chad on 3/9/15.
 */
public class OpinionDistribution extends StatModule {
    private HashMap<Double, Double[]> densities = new HashMap<Double, Double[]>();
    private HashMap<Simulator, Double> sims = new HashMap<Simulator, Double>();
    private HashMap<Double, Integer> frequencies = new HashMap<Double, Integer>();

    // number of buckets to place opinions into
    private int B = 101;

    public OpinionDistribution(Statistics parent) {
        super(parent);
    }

    @Override
    public void hookSimulationBegin() {
    }

    @Override
    public void hookSimulationEnd() {
        System.out.println("Printing out density data...");
        File file = new File(stats.getDataFolder() + "opinion_distribution.dat");
        try {
            BufferedWriter f = new BufferedWriter(new FileWriter(file));
            List<Double> keys = new ArrayList<Double>();
            keys.addAll(densities.keySet());
            Collections.sort(keys);
            for (Double key : keys) {
                Double[] b = densities.get(key);
                int a = frequencies.get(key);
                for (int i = 0; i < B; i++) {
                    f.write(String.format("%10.6f %10.8f %10.8f\n", key,
                            (1. * i) / (B - 1), b[i] / a));
                }
            }
            f.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String[][] args = new String[][]{
                new String[]{"outfile",
                        stats.getDataFolder() + "opinion_distribution"},
                new String[]{"infile",
                        stats.getDataFolder() + "opinion_distribution.dat"},
                new String[]{"independent", stats.indp.toString()},
                new String[]{"gname", "Opinion Density"}};
        Gnuplot.plotFile("opinion_distribution.plt", args);

        System.out.println("Done!");
    }

    @Override
    public void hookTrialBegin(Simulator sim, double indpVal, int trial) {
        sims.put(sim, indpVal);
    }

    public void updateDistribution(Simulator sim, Double key) {
        Double[] b;

        if (!densities.containsKey(key)) {
            b = new Double[B];
            for (int i = 0; i < B; i++)
                b[i] = 0.;
            densities.put(key, b);
            frequencies.put(key, 1);
        } else {
            b = densities.get(key);
            frequencies.replace(key, frequencies.get(key) + 1);
        }

        for (Agent a : sim.getAgents()) {
            b[(int) Math.round(a.getOpinion() * (B - 1))]++;
        }
    }

    @Override
    public void hookTrialEnd(Simulator sim) {
        updateDistribution(sim, sims.get(sim));
        sims.remove(sim);
    }

    @Override
    public void hookRoundBegin(Simulator sim, int round) {
    }

    @Override
    public void hookRoundEnd(Simulator sim, int round) {
        if (stats.indp != Independent.TIME) return;
        updateDistribution(sim, (double) round);
    }
}
