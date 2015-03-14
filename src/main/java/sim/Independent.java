package sim;

/**
 * Created by Chad on 3/8/15.
 */
public enum Independent {
    EPSILON, DELTA, MU, DEGREE, TIME, NONE;

    @Override
    public String toString() {
        String str = super.toString().toLowerCase();
        return str.substring(0,1).toUpperCase()+str.substring(1);
    }
}
