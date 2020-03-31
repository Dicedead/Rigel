package ch.epfl.rigel;

import ch.epfl.rigel.math.Interval;

/**
 * Utility class for value validity checking
 *
 * @author Alexandre Sallinen (303162)
 * @author Salim Najib (310003)
 */
public final class Preconditions {

    private Preconditions(){}

    /**
     * Validity checking method
     * @param argIsTrue Boolean to check
     */
    public static void checkArgument(boolean argIsTrue){
        if (!argIsTrue) {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Checking value containment in an interval, returns value if it passes the check
     *
     * @param interval Said interval
     * @param value Said value
     */
    public static double checkInInterval(Interval interval, double value) {
        if (!interval.contains(value)) {
            throw new IllegalArgumentException();
        }
        return value;
    }

}
