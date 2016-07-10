package edu.utulsa.masters.opdyn.sim;

/**
 * The set of currently "supported" independent variables. Some of these might crash.
 */
public enum Independent {
    EPSILON, DELTA, MU, DEGREE, TIME, NONE;

    @Override
    public String toString() {
        String str = super.toString().toLowerCase();
        return str.substring(0,1).toUpperCase()+str.substring(1);
    }
}
