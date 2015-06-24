package cutil;

import sim.Independent;

import java.io.IOException;
import java.util.Arrays;

/**
 * Created by Chad on 3/9/15.
 */
public class Test {
    public static void main(String[] args) {
        System.out.println(Independent.DEGREE.toString());
        Runtime rt = Runtime.getRuntime();
        try {
            String[] command = new String[]{"cmd", "/C", "echo", "\"hello\""};
            // Debug.println(1, Arrays.toString(command));
            System.out.println(Arrays.toString(command));
            rt.exec(command);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
