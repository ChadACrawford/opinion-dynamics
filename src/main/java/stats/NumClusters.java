package stats;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import sim.Interact;
import sim.InteractBC;
import sim.InteractM1;
import sim.InteractM2;
import sim.Network;
import sim.SimNetwork;
import sim.Simulator;

/**
 * Created by Chad on 3/10/15.
 */
public class NumClusters extends StatModule {
	HashMap<Double, Double> clusters = new HashMap<Double, Double>();
	HashMap<Simulator, Double> sims = new HashMap<Simulator, Double>();
	private HashMap<Double, Integer> frequencies = new HashMap<Double, Integer>();

	public NumClusters(Statistics parent) {
		super(parent);
	}

	private double getEpsilon(Interact I) {
		if (I instanceof InteractBC)
			return ((InteractBC) I).EPSILON;
		else if (I instanceof InteractM1)
			return ((InteractM1) I).EPSILON;
		else if (I instanceof InteractM2)
			return ((InteractM2) I).EPSILON;
		else
			return 0;
	}

	@Override
	public void hookSimulationBegin() {
	}

	@Override
	public void hookSimulationEnd() {
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(new File(
					stats.getDataFolder() + "num_clusters.dat")));
			List<Double> keys = new ArrayList<Double>(clusters.keySet());
			Collections.sort(keys);
			for (Double i : keys) {
				bw.write(String.format("%8.6f %10.4f\n", i, clusters.get(i)
						/ frequencies.get(i)));
			}
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.print("Printing out clusters... ");
		String[][] args = new String[][] {
				new String[] { "outfile",
						stats.getDataFolder() + "num_clusters" },
				new String[] { "infile",
						stats.getDataFolder() + "num_clusters.dat" },
				new String[] { "independent", stats.indp.toString() } };
		Gnuplot.plotFile("num_clusters.plt", args);
		System.out.println("Done!");
	}

	@Override
	public void hookTrialBegin(Simulator sim, double indpVal, int trial) {
		sims.put(sim, indpVal);
	}

	@Override
	public void hookTrialEnd(Simulator sim) {
		double indpVal = sims.get(sim);
		Network N = ((SimNetwork) sim).getNetwork();
		int c = N.calcClusters(getEpsilon(sim.I)).size();
		if (clusters.containsKey(indpVal)) {
			clusters.replace(indpVal, clusters.get(indpVal) + c);
			frequencies.replace(indpVal, frequencies.get(indpVal) + 1);
		} else {
			clusters.put(indpVal, (double) c);
			frequencies.put(indpVal, 1);
		}
		sims.remove(sim);
	}

	@Override
	public void hookRoundBegin(Simulator sim, int round) {
	}

	@Override
	public void hookRoundEnd(Simulator sim, int round) {
	}
}
