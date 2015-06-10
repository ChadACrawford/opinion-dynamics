package stats;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import sim.Driver;
import sim.Independent;
import sim.Simulator;

/**
 * Created by Chad on 3/8/15.
 */
public class Statistics {
	private String dataFolder;
	private Driver driver;

	public final Independent indp;
	private final List<StatModule> modules = new LinkedList<StatModule>();

	public Statistics(Independent indp, Driver driver, Properties config) {
		this.indp = indp;
		this.driver = driver;
		readConfig(config);

		// Get the current date/time
		DateFormat dt = new SimpleDateFormat("yyyy_MM_dd_HH_mm/");
		Calendar cal = Calendar.getInstance();
		dataFolder = "data/sim_" + dt.format(cal.getTime());
		System.out.println(dataFolder);

		File f = new File(dataFolder);
		if (!f.isDirectory())
			f.mkdir();

		try {
			printParamFile(config);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void addModule(StatModule m) {
		modules.add(m);
	}

	private void printParamFile(Properties p) throws IOException {
		File pf = new File(dataFolder + "params.txt");
		BufferedWriter bf = new BufferedWriter(new FileWriter(pf));
		bf.write(p.toString());
		bf.close();
	}

	public void onSimulationBegin() {
		for (StatModule m : modules)
			m.hookSimulationBegin();
	}

	public void onSimulationEnd() {
		for (StatModule m : modules)
			m.hookSimulationEnd();
	}

	public void onTrialBegin(Simulator sim, double indpVal, int trial) {
		for (StatModule m : modules)
			m.hookTrialBegin(sim, indpVal, trial);
	}

	public void onTrialEnd(Simulator sim) {
		for (StatModule m : modules)
			m.hookTrialEnd(sim);
	}

	public void onRoundBegin(Simulator sim, int round) {
		for (StatModule m : modules)
			m.hookRoundBegin(sim, round);
	}

	public void onRoundEnd(Simulator sim, int round) {
		for (StatModule m : modules)
			m.hookRoundEnd(sim, round);
	}

	public String getDataFolder() {
		return dataFolder;
	}

	public void readConfig(Properties p) {
		String propModules = p.getProperty("StatModules").toUpperCase();
		List<String> modules = Arrays.asList(propModules.split(","));
		if (modules.contains("OPINION_DISTRIBUTION")) {
			this.addModule(new OpinionDistribution(this));
		}
		if (modules.contains("NUM_CLUSTERS")) {
			this.addModule(new NumClusters(this));
		}
	}
}
