package ch.epfl.rigel;

import ch.epfl.rigel.logging.RigelLogger;
import ch.epfl.rigel.math.Interval;

/**
 * Utility class for value validity checking
 *
 * @author Alexandre Sallinen (303162)
 * @author Salim Najib (310003)
 */
public final class Preconditions {

    //Non instantiable
    private Preconditions(){ throw new UnsupportedOperationException(); }
    /*
      The constructor of a non instantiable class throwing a UO Exception rather than just being private:
         a) guarantees that the following code does not create an instance, and
         b) is immune to reflection (Field.setAccessible)
     */

    /**
     * Validity checking method
     * @param argIsTrue (boolean) Boolean to check
     */
    public static void checkArgument(boolean argIsTrue){
        if (!argIsTrue) {
            throw new IllegalArgumentException();
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
            RigelLogger.getBackendLogger().severe("Value : " + value + " is not in interval " + interval.toString());
            throw new IllegalArgumentException();
        }
        return value;
    }

}
