package cutil;

import java.io.File;

/**
 * Created by Moin on 7/2/2015.
 */
public class HelperMethods {
    public static String getFileName(File f) {
        String name = f.getName();
        int pos = name.lastIndexOf(".");
        if (pos > 0) {
            name = name.substring(0, pos);
        }
        return name;
    }
}
