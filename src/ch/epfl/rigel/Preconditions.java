package ch.epfl.rigel;

import ch.epfl.rigel.math.Interval;

/**
 * Utility class for value validity checking and correcting
 *
 * @author Alexandre Sallinen (303162)
 * @author Salim Najib (310003)
 */
public final class Preconditions {

    private static final double EPSILON = 1e-4;
    private static final double COMPARISON_EPSILON = EPSILON/1.75;
    private static final double MIN_EPSILON = -EPSILON;

    //Non instantiable
    private Preconditions(){ throw new UnsupportedOperationException("Fatal error: tried to instantiate non " +
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

    /**
     * Added in step 12: some values in the program (especially but not solely denominators) should not reach a value
     * too close to zero to avoid infinity situations which are distasteful to the eye
     *
     * @param value (double) input value
     * @return (double) value itself if it was not within an epsilon of zero, else, just over zero if value was positive,
     *         just under zero if value was strictly negative.
     */
    public static double epsilonIfZero(double value) {
        if (Math.abs(value) < COMPARISON_EPSILON) {
            return ((value >= 0) ? EPSILON : MIN_EPSILON);
        }
         else { return value; }
    }

}
