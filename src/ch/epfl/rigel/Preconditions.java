package ch.epfl.rigel;

import ch.epfl.rigel.math.Interval;

/**
 * Utility class for value validity checking
 *
 * @author Alexandre Sallinen (303162)
 * @author Salim Najib (310003)
 */
public final class Preconditions {

    //Non instantiable
    private Preconditions(){ throw new UnsupportedOperationException("Fatal error: tried to instantiate non" +
            "instantiable class Preconditions."); }

    /**
     * Validity checking method
     *
      * @param argIsTrue (boolean) Boolean to check
     */
    public static void checkArgument(boolean argIsTrue) {
        checkArgument(argIsTrue, "Invalid argument.");
    }

    /**
     * Validity checking method
     * @param argIsTrue (boolean) Boolean to check
     * @param errorString (String) String attached to the IAE thrown if given condition is false
     */
    public static void checkArgument(boolean argIsTrue, String errorString){
        if (!argIsTrue) {
            throw new IllegalArgumentException(errorString);
        }
    }

    /**
     * Checking value containment in an interval, returns value if it passes the check
     *
     * @param interval (Interval) Said interval
     * @param value (double) Said value
     */
    public static double checkInInterval(Interval interval, double value) {
        if (!interval.contains(value)) {
            throw new IllegalArgumentException("Value not in given interval.");
        }
        return value;
    }

}
