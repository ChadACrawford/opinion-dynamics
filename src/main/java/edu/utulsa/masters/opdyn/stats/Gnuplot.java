package edu.utulsa.masters.opdyn.stats;

import edu.utulsa.masters.opdyn.sim.Debug;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

/**
 * Helper method for plotting data with Gnuplot. You will need to implement a GNUPlot .plt file in the data/ directory,
 * and then call GNUPlot using that input file.
 *
 * To use GNUplot, you will need to install it and add it to your PATH.
 */
public class Gnuplot {
    /**
     * Plots a GNUPlot file.
     * @param inputFile The .plt file.
     * @param args Any extra argument pairs that should be passed to the file (for example, naming the title or
     *             something, or specifying the file that has your data)
     */
    public static void plotFile(String inputFile, String[][] args) {

        try {
            String argstring = "";
            for(String[] s: args) argstring += String.format("%s='%s';", s[0], s[1]);
            if(isWindows())
                callGnuplotWindows(inputFile, argstring);
            else if(isMac())
                System.err.println("Mac GNUPlot call not implemented. No plots will be produced.");
            else if(isUnix())
                callGnuplotLinux(inputFile, argstring);
            else
                System.err.println("OS not supported for GNUPlot call. No plots will be produced.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void callGnuplotWindows(String inputFile, String argstring) throws IOException {
        Runtime rt = Runtime.getRuntime();
        String[] command = new String[] {
                "cmd",
                "/C",
                "start",
                "gnuplot",
                "-e",
                String.format("\"%s\"",argstring),
                String.format("data/%s",inputFile)
        };
        //Debug.println(1, Arrays.toString(command));
        rt.exec(command);
    }

    private static void callGnuplotLinux(String inputFile, String argstring) throws IOException {
        Runtime rt = Runtime.getRuntime();
        String[] command = new String[] {
                "/bin/sh",
                "-c",
                "gnuplot -e " + String.format("\"%s\"",argstring)
                        + " " + String.format("data/%s",inputFile),
        };
//        String command1 = "gnuplot -e \"" + argstring + "\" data/" + inputFile + " && pwd";
        System.out.println(Arrays.toString(command));
        System.out.println(System.getProperty("user.dir"));
//        System.out.println(command1);
        Process p = rt.exec(command);
        String line;
        BufferedReader in = new BufferedReader(
                new InputStreamReader(p.getInputStream()) );
        while ((line = in.readLine()) != null) {
            System.out.println(line);
        }
        in.close();
    }

    private static String os = System.getProperty("os.name").toLowerCase();

    private static boolean isWindows() {
        return os.contains("win");
    }

    private static boolean isMac() {
        return os.contains("mac");
    }

    private static boolean isUnix() {
        return os.contains("nix") || os.contains("nux") || os.contains("aix");
    }
}
