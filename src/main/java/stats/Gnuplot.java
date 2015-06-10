package stats;

import java.io.IOException;

/**
 * Created by Chad on 6/9/2015.
 */
public class Gnuplot {
	public static void plotFile(String inputFile, String[][] args) {
		Runtime rt = Runtime.getRuntime();
		try {
			String argstring = "";
			for (String[] s : args)
				argstring += String.format("%s='%s';", s[0], s[1]);
			String[] command = new String[] { "cmd", "/C", "start", "gnuplot",
					"-e", String.format("\"%s\"", argstring),
					String.format("data/%s", inputFile) };
			// Debug.println(1, Arrays.toString(command));
			rt.exec(command);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
