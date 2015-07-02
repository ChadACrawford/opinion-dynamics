package sim;

import stats.Statistics;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.Random;

/**
 * Created by Chad on 3/8/15.
 */
public class Driver {
    Statistics stats;
    int TRIALS;
    int NODES;
    boolean TRACK_ACTIVITY;

    Independent indp;
    double indpStart, indpEnd, indpStep, indpCurrent;

    NetworkType networkType;
    NetworkGenerator netGen;

    InteractionType interactionType;
    Interact I;

    Random rand;
    long seed;

    private double OPD_THRESHOLD;

    public void run() {
        stats.onSimulationBegin();
        Debug.println(-10, "Running simulation.");
        do {
            Debug.format(-1, "Running trialset for %s = %6.4f\n", indp,
                    indpCurrent);
            runTrialSet(indpCurrent);
            indpCurrent += indpStep;
        } while (indpCurrent <= indpEnd);
        Debug.println(-10, "Finished simulation");
        stats.onSimulationEnd();
    }

    public void runTrialSet(double indpVal) {
        Interact I2 = (Interact) I.updateValue(indp, indpVal);
        NetworkGenerator NG2 = (NetworkGenerator) netGen.updateValue(indp,
                indpVal);
        for (int t = 0; t < TRIALS; t++) {
            runTrial(NG2, I2, t);
        }
    }

    public void runTrial(NetworkGenerator ng, Interact i, int trialNum) {
        Network N = ng.generate(NODES);
        Simulator sim = new SimNetwork(i, TRACK_ACTIVITY, seed, N);
        stats.onTrialBegin(sim, indpCurrent, trialNum);
        int round = 1;
        int pass = 0;
        do {
            stats.onRoundBegin(sim, round);
            sim.runRound();
            stats.onRoundEnd(sim, round);
            Debug.format(20, "Round %d %10.8f\n", round,
                    sim.getTotalOpinionDifference());
            if (sim.getTotalOpinionDifference() <= OPD_THRESHOLD)
                pass++;
            else
                pass = 0;
            round++;
        } while (pass < 100);
        stats.onTrialEnd(sim);
        Debug.format(0, "Finished trial %d in %d rounds.\n", trialNum, round);
    }

    enum NetworkType {
        Random, Complete, BarabasiAlbert, WattsStrogatz, Group
    }

    enum InteractionType {
        BC, M1, M2
    }

    public void loadConfig(File f) throws IOException {
        Properties p = new Properties();
        p.load(new FileInputStream(f));

        // Get Indepdent variable
        String propIndp = p.getProperty("Independent", "NONE").toUpperCase();
        if (propIndp.equals("EPSILON"))
            indp = Independent.EPSILON;
        else if (propIndp.equals("DELTA"))
            indp = Independent.DELTA;
        else if (propIndp.equals("MU"))
            indp = Independent.MU;
        else if (propIndp.equals("DEGREE"))
            indp = Independent.DEGREE;
        else if (propIndp.equals("TIME"))
            indp = Independent.TIME;
        else
            indp = Independent.NONE;

        // Get independent sampling rate
        if (indp != Independent.NONE) {
            String propIndpStart = p.getProperty("IndependentStartValue", "0");
            String propIndpEnd = p.getProperty("IndependentStopValue", "1");
            String propIndpStep = p.getProperty("IndependentStepValue", "0.01");
            indpStart = Double.parseDouble(propIndpStart);
            indpEnd = Double.parseDouble(propIndpEnd);
            indpStep = Double.parseDouble(propIndpStep);
            indpCurrent = indpStart;
        }

        // Get # of trials, other variables
        String propTrials = p.getProperty("Trials", "10");
        String propOPDThreshold = p.getProperty("OpinionDifferenceThreshold",
                "1E-4");
        String propNodes = p.getProperty("Nodes", "1000");
        String propSeed = p.getProperty("Seed");
        String propTrackActivity = p.getProperty("TrackActivity", "False");
        TRIALS = Integer.parseInt(propTrials);
        OPD_THRESHOLD = Double.parseDouble(propOPDThreshold);
        NODES = Integer.parseInt(propNodes);
        if (propSeed != null)
            seed = Long.parseLong(propSeed);
        else
            seed = System.currentTimeMillis();
        rand = new Random(seed);
        TRACK_ACTIVITY = Boolean.parseBoolean(propTrackActivity);

        // Get network style
        String propNetworkType = p.getProperty("NetworkType", "COMPLETE")
                .toUpperCase();
        if (propNetworkType.equals("RANDOM")) {
            networkType = NetworkType.Random;
            String propRD = p.getProperty("Random_D", "10");
            int D = Integer.parseInt(propRD);
            netGen = new NetworkGenerator.NGRandom(D);
        } else if (propNetworkType.equals("BARABASIALBERT")) {
            networkType = NetworkType.BarabasiAlbert;
            String propBAM = p.getProperty("BarabasiAlbert_M", "2");
            int M = Integer.parseInt(propBAM);
            netGen = new NetworkGenerator.NGScaleFree(M);
        } else if (propNetworkType.equals("WATTSSTROGATZ")) {
            networkType = NetworkType.WattsStrogatz;
            String propWSK = p.getProperty("WattsStrogatz_K", "4");
            String propWSB = p.getProperty("WattsStrogatz_B", "0.5");
            int K = Integer.parseInt(propWSK);
            double B = Double.parseDouble(propWSB);
            netGen = new NetworkGenerator.NGSmallWorld(K, B);
        } else if (propNetworkType.equals("GROUP")) {
            networkType = NetworkType.Group;
            String propGK = p.getProperty("Group_K", "20");
            int K = Integer.parseInt(propGK);
            netGen = new NetworkGenerator.NGGroup(K);
        } else {
            networkType = NetworkType.Complete;
            netGen = new NetworkGenerator.NGComplete();
        }

        // Get interaction type
        String propInteractionType = p.getProperty("Interaction", "BC");
        if (propInteractionType.equals("M1")) {
            interactionType = InteractionType.M1;
            double E = Double.parseDouble(p.getProperty("EPSILON"));
            double U = Double.parseDouble(p.getProperty("MU"));
            double D = Double.parseDouble(p.getProperty("DELTA"));
            I = new InteractM1(E, U, D);
        } else if (propInteractionType.equals("M2")) {
            interactionType = InteractionType.M2;
            double E = Double.parseDouble(p.getProperty("EPSILON"));
            double U = Double.parseDouble(p.getProperty("MU"));
            double D = Double.parseDouble(p.getProperty("DELTA"));
            I = new InteractM2(E, U, D);
        } else {
            interactionType = InteractionType.BC;
            double E = Double.parseDouble(p.getProperty("EPSILON"));
            double U = Double.parseDouble(p.getProperty("MU"));
            I = new InteractBC(E, U);
            Debug.format(-10, "%6.4f %6.4f", E, U);
        }

        stats = new Statistics(indp, this, p);
    }

    public static void main(String[] args) throws IOException {
        String propertiesFile = args[0];
        Driver d = new Driver();
        d.loadConfig(new File(propertiesFile));
        d.run();
    }
}
